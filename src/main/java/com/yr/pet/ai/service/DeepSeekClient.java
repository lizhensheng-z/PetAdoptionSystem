package com.yr.pet.ai.service;

import cn.hutool.core.exceptions.ValidateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.ai.cache.ChatHistoryCache;
import com.yr.pet.ai.config.DeepSeekConfig;
import com.yr.pet.ai.model.entity.QuestionRecordDO;
import com.yr.pet.ai.model.entity.SessionDO;
import com.yr.pet.ai.model.enums.DeepSeekErrorCode;
import com.yr.pet.ai.model.req.AiChatMessage;
import com.yr.pet.ai.model.req.AiChatRequest;
import com.yr.pet.ai.model.vo.BalanceVO;
import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.http.codec.ServerSentEvent;

import static com.yr.pet.ai.config.DeepSeekConfig.*;
import static com.yr.pet.ai.model.req.AiChatRequest.OPEN_STREAM;

@Slf4j
@Component
public class DeepSeekClient {
    @Resource
    private ChatHistoryCache chatHistoryCache;
    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private DeepSeekConfig deepSeekConfig;
    // 注入Jackson的ObjectMapper（用于序列化请求体）
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 创建一个WebClient实例,避免每次请求都创建新的实例
     */
    @Resource
    private WebClient webClient;
    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HEADER, BEARER+deepSeekConfig.getApiKey())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMinutes(5)) // 响应超时（根据业务调整，如5分钟）
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 连接超时5秒
                ))
                .build();
    }

    // 暂时先用内存：累积AI流式响应的临时容器（key: questionRequestId，value: 累积的完整回复）
    public static final Map<Long, StringBuilder> aiResponseCache = new ConcurrentHashMap<>();


    /**
     *  构建AI聊天请求对象
     * @param content 请求内容
     * @return AiChatRequest对象
     */
    private @NotNull AiChatRequest getAiChatRequest(String content, Long userId) {
        // 1. 获取历史对话
        List<AiChatMessage> history = chatHistoryCache.getHistory(userId);

        // 2. 构建新的用户消息
        AiChatMessage userMessage = new AiChatMessage(AiChatMessage.ROLE_USER, content);

        // 3. 组装完整消息列表（系统消息 + 历史消息 + 当前消息）
        List<AiChatMessage> messages = new ArrayList<>(history);  // 追加历史对话
        messages.add(userMessage); // 追加当前用户消息
        AiChatRequest request = new AiChatRequest();
        request.setMessages(messages);
        request.setModel(AiChatRequest.DEFAULT_MODEL);// 模型
        request.setStream(OPEN_STREAM);// 是否开启流式响应
        request.setMax_tokens(AiChatRequest.DEFAULT_MAX_TOKENS);// 最大返回token数
        request.setTemperature(AiChatRequest.DEFAULT_TEMPERATURE);// 随机性
        //返回前把当前用户提问存入历史
        chatHistoryCache.appendMessage(userId, userMessage);
        return request;
    }

    /**
     * 聊天接口
     * @param content 用户输入内容
     * @param sessionDO 会话
     * @return 流式SSE响应结果
     */
    public Flux<ServerSentEvent<String>> chatCompletions(String content, SessionDO sessionDO) {
        Long userId = UserContext.getUserId();
        AiChatRequest request = getAiChatRequest(content,userId);
        Flux<ServerSentEvent<String>> flux = getAuthorization(content,request,sessionDO);
        return flux;
    }


    /**
     * 落库并发送请求
     * @param content 用户输入内容
     * @param request 构建的DeepSeek请求体
     * @param sessionDO 会话
     * @return 流式SSE响应（转发给前端）
     */
    private @NotNull Flux<ServerSentEvent<String>>
    getAuthorization(String content, AiChatRequest request, SessionDO sessionDO) {
        Long questionRequestId;
        Long sessionId = sessionDO.getSessionId();
        try {
            // 1. 序列化请求体并落库用户问题记录
            String requestBodyJson = objectMapper.writeValueAsString(request);
            log.info("sessionId:{},向DeepSeek发送请求，RequestBody: {}",sessionId, requestBodyJson);
            questionRequestId = questionRecordService.saveUserQuestionRecord(content, sessionId, requestBodyJson);
            // 初始化累积容器（用于拼接AI的流式回复）
            aiResponseCache.put(questionRequestId, new StringBuilder());
        } catch (JsonProcessingException e) {
            log.error("序列化请求体失败", e);
            // 这里返回一条错误SSE事件
            ServerSentEvent<String> errorEvent = ServerSentEvent.<String>builder()
                    .event("error")
                    .data(DeepSeekErrorCode.ERROR_TO_USER)
                    .build();
            return Flux.just(errorEvent);
        }
        // 保存questionRequestId到局部变量，用于lambda中捕获（避免null问题）
        Long finalQuestionRequestId = questionRequestId;
        Flux<ServerSentEvent<String>> flux = webClient.post()
                .uri(URI)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(
                        //TODO 处理4xx和5xx错误响应
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        getResponseMonoFunction(finalQuestionRequestId)
                )
                .bodyToFlux(String.class)
                .flatMap(result -> handleResult(result, finalQuestionRequestId,sessionDO))
                // 处理请求异常（如DeepSeek连接失败、超时）
                .onErrorResume(e -> {
                    handleErrorResume(e, finalQuestionRequestId);
                    ServerSentEvent<String> errorEvent = ServerSentEvent.<String>builder()
                            .event("error")
                            .data(DeepSeekErrorCode.ERROR_TO_USER)
                            .build();
                    return Flux.just(errorEvent);
                })
                // 处理前端主动取消请求
                .doOnCancel(() -> handleOnCancel(finalQuestionRequestId));
        return flux;
    }

    /**
     * 处理流式响应块：解析content并封装成SSE事件
     */
    private Flux<ServerSentEvent<String>> handleResult(String result, Long questionRequestId,SessionDO sessionDO) {
        // 1. 从缓存获取当前请求的累积器（若不存在，直接返回空）
        StringBuilder aiResult = aiResponseCache.get(questionRequestId);
        if (aiResult == null) {
            log.warn("未找到累积器，questionRequestId: {}", questionRequestId);
            return Flux.empty();
        }

        // 2. 处理结束标记（[DONE]）
        if ("[DONE]".equals(result)) {
            // 清理缓存（避免内存占用）
            aiResponseCache.remove(questionRequestId);

            return Flux.empty(); // 结束流式响应
        }

        // 3. 处理正常响应块（解析content并累积）
        try {
            JsonNode jsonNode = objectMapper.readTree(result);

            String content = jsonNode.get("choices").get(0).get("delta").get("content").asText("");

            // 累积回复片段（拼接完整内容）
            if (!content.isEmpty()) {
                aiResult.append(content);
            }
            checkIsFinish(questionRequestId, jsonNode);

            ServerSentEvent<String> event = ServerSentEvent.<String>builder()
                    .event("message")
                    .data(content)
                    .build();
            return Flux.just(event);
        } catch (Exception e) {
            log.error("解析响应块失败，result: {}", result, e);
            return Flux.empty();
        }
    }


    /**
     * 根据响应状态码处理错误响应
     * @param finalQuestionRequestId 关联的用户问题ID
     * @return 处理函数，返回Mono<Throwable>
     */
    private @NotNull Function<ClientResponse, Mono<? extends Throwable>> getResponseMonoFunction(Long finalQuestionRequestId) {
        return response -> {
            int statusCode = response.statusCode().value();
            // 1. 通过状态码匹配枚举实例
            DeepSeekErrorCode errorCode = DeepSeekErrorCode.fromStatusCode(statusCode);
            // 2. 解析API返回的原始错误信息（可选，用于补充详情）
            return response.bodyToMono(JsonNode.class)
                    .defaultIfEmpty(objectMapper.createObjectNode())
                    .flatMap(errorJson -> {
                        handleErrorByCode(finalQuestionRequestId, errorJson, errorCode);
                        // 5. 返回前端友好提示（枚举的简洁提示）
                        return Mono.error(new ValidateException(DeepSeekErrorCode.ERROR_TO_USER));
                    });
        };
    }
    /**
     * 根据错误码处理错误响应
     * @param finalQuestionRequestId 关联的用户问题ID
     * @param errorJson API返回的错误JSON
     * @param errorCode DeepSeek错误码枚举
     */
    private void handleErrorByCode(Long finalQuestionRequestId, JsonNode errorJson, DeepSeekErrorCode errorCode) {
        // 提取API返回的错误消息
        String apiErrorMsg = errorJson.has("error") ?
                errorJson.get("error").get("message").asText("") : "";

        // 3. 日志记录（结合枚举的详细信息+API原始消息）
        log.error("DeepSeek请求错误，questionRequestId: {}，{}，API原始信息：{}",
                finalQuestionRequestId, errorCode.getDetailMsg(), apiErrorMsg);
        String remark = "DeepSeek请求错误，questionRequestId:"+ finalQuestionRequestId;
        // 4. 落库错误记录（使用枚举信息）
        try{
            if (finalQuestionRequestId != null) {
                questionRecordService.saveResponseRecord(
                        finalQuestionRequestId,
                        errorJson,
                        QuestionRecordDO.STATUS_FAIL,
                        //TODO 落库内容：枚举详情+API原始消息 太长了 ->自定义remark
                        remark
                );
            }
        }finally {
            aiResponseCache.remove(finalQuestionRequestId); // 清理缓存
        }
    }

    /**
     * 处理前端取消请求
     * @param finalQuestionRequestId 用户问题id
     */
    private void handleOnCancel(Long finalQuestionRequestId) {
        try{
            log.info("前端取消请求，questionRequestId: {}", finalQuestionRequestId);
            String remark = "用户取消了请求";
            if (finalQuestionRequestId != null) {
                questionRecordService.saveResponseRecord(
                        finalQuestionRequestId,
                        null,
                        QuestionRecordDO.STATUS_INTERRUPT,
                        remark
                );
            }
        } catch (ValidateException ex) {
            throw new ValidateException(DeepSeekErrorCode.ERROR_TO_USER);
        }finally {

            aiResponseCache.remove(finalQuestionRequestId);
        }
    }
    /**
     * 处理请求异常
     * @param e 异常对象
     * @param finalQuestionRequestId 关联的用户问题ID
     */
    private void handleErrorResume(Throwable e, Long finalQuestionRequestId) {
        // 异常时落库错误状态
        try {
            log.error("DeepSeek请求异常，questionRequestId: {}", finalQuestionRequestId, e);
            String remark = e.getMessage();
            if (finalQuestionRequestId != null) {
                questionRecordService.saveResponseRecord(
                        finalQuestionRequestId,
                        null,
                        QuestionRecordDO.STATUS_FAIL,
                        remark // 记录异常信息
                );
            }
        } catch (ValidateException ex) {
            throw new ValidateException(DeepSeekErrorCode.ERROR_TO_USER);
        }finally {
            aiResponseCache.remove(finalQuestionRequestId); // 清理缓存
        }
    }
    /**
     * 检查是否为结束块并落库完整回复
     * @param questionRequestId 关联的用户问题ID
     * @param jsonNode 当前响应块的JSON节点
     */
    private void checkIsFinish(Long questionRequestId, JsonNode jsonNode) {
        // 4. 处理token用量（注：deepseek在"[DONE]"前的最后一块返回）
        if (jsonNode.get("usage") != null) {
            log.info("jsonNode: {}", jsonNode);
            // 落库完整回复（状态为成功）
            questionRecordService.saveResponseRecord(
                    questionRequestId,
                    jsonNode,
                    QuestionRecordDO.STATUS_SUCCESS,
                    QuestionRecordDO.RESPONSE_SUCCESS
            );
        }
    }
    private final OkHttpClient okHttp = new OkHttpClient();
    /**
     * 查询账户余额（内部统一封装）
     */
    public BalanceVO getBalance() {
        Request request = new Request.Builder()
                .url(BASE_URL+BALANCE_URI)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                .build();

        try (Response response = okHttp.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("查询余额失败，HTTP={}", response.code());
                throw new ValidateException("查询余额失败");
            }
            String json = null;
            if (response.body() != null) {
                json = response.body().string();
            }
            JsonNode node = new ObjectMapper().readTree(json);

            // 解析 JSON
            JsonNode info = node.get("balance_infos").get(0);
            BalanceVO balanceVO = new BalanceVO();
            balanceVO.setAvailable(node.get("is_available").asBoolean());
            balanceVO.setTotalBalance(new BigDecimal(info.get("total_balance").asText()));
            balanceVO.setGrantedBalance(new BigDecimal(info.get("granted_balance").asText()));
            balanceVO.setToppedUpBalance(new BigDecimal(info.get("topped_up_balance").asText()));
            return balanceVO;

        } catch (IOException e) {
            log.error("查询余额异常", e);
            throw new ValidateException("查询余额异常");
        }
    }
}

