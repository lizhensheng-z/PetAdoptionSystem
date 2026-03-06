package com.yr.pet.ai.controller;

import com.yr.pet.ai.model.entity.SessionDO;
import com.yr.pet.ai.model.vo.BalanceVO;
import com.yr.pet.ai.service.DeepSeekClient;
import com.yr.pet.ai.service.QuestionTemplateService;
import com.yr.pet.ai.service.SessionService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/api/ai/deepseek")
@Slf4j
public class DeepSeekController {

    @Resource
    private DeepSeekClient deepSeekClient;

    @Resource
    private SessionService sessionService;

    @Resource
    private QuestionTemplateService questionTemplateService;

    @GetMapping(value = "/chatCompletions", produces = "text/event-stream;charset=utf-8")
    public SseEmitter chatCompletions(@NotNull @RequestParam(value = "content") String content) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        // 设置超时回调
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时");
            emitter.complete();
        });

        // 设置错误回调
        emitter.onError(throwable -> {
            log.error("SSE连接异常", throwable);
        });

        try {
            // 创建会话
            String title = content.length() > 10 ? content.substring(0, 10) : content;
            SessionDO sessionDO = sessionService.createSession(title);

            // 先查询本地模板库
            String templateResponse = questionTemplateService.selectByQuestion(content, sessionDO.getSessionId());
            if (templateResponse != null) {
                // 命中模板，直接返回
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(templateResponse));
                emitter.complete();
                return emitter;
            }

            // 未命中模板，调用AI接口
            Flux<ServerSentEvent<String>> flux = deepSeekClient.chatCompletions(content, sessionDO);

            // 订阅Flux流（Flux本身是异步的，subscribe不会阻塞当前线程）
            flux.subscribe(
                    event -> {
                        try {
                            String eventName = event.event() != null ? event.event() : "message";
                            String data = event.data();
                            if (data != null && !data.isEmpty()) {
                                emitter.send(SseEmitter.event()
                                        .name(eventName)
                                        .data(data));
                            }
                        } catch (IOException e) {
                            log.error("发送SSE事件失败", e);
                            emitter.completeWithError(e);
                        }
                    },
                    error -> {
                        log.error("AI流式响应异常", error);
                        emitter.completeWithError(error);
                    },
                    () -> {
                        log.info("AI流式响应完成");
                        emitter.complete();
                    }
            );

        } catch (Exception e) {
            log.error("处理AI聊天请求异常", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @GetMapping("/account/balance")
    public BalanceVO getBalance() {
        return deepSeekClient.getBalance();
    }
}
