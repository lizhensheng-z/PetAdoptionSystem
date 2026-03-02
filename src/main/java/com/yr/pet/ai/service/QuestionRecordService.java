package com.yr.pet.ai.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.ai.cache.ChatHistoryCache;
import com.yr.pet.ai.mapper.QuestionRecordMapper;
import com.yr.pet.ai.model.entity.QuestionRecordDO;
import com.yr.pet.ai.model.req.AiChatMessage;
import com.yr.pet.ai.model.vo.QuestionRecordVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.yr.pet.ai.model.entity.QuestionRecordDO.STATUS_SUCCESS;


/**
 * @author 李振生
 */
@Slf4j
@Service
public class QuestionRecordService extends ServiceImpl<QuestionRecordMapper, QuestionRecordDO> {
    @Resource
    private ChatHistoryCache chatHistoryCache;
    @Resource
    private QuestionRecordMapper questionRecordMapper;
    /**
     * 注意：只保存用户提问记录
     * @param content 问题内容
     * @param sessionId 会话ID
     * @return questionId 当前用户问题唯一ID
     */
    public Long saveUserQuestionRecord(String content, Long sessionId,String requestParam) {
        Long userId = UserContext.getUserId();
        QuestionRecordDO questionRecordDO = new QuestionRecordDO();
        questionRecordDO.setUserId(userId);
        questionRecordDO.setType(QuestionRecordDO.TYPE_QUESTION);
        questionRecordDO.setSessionId(sessionId);
        questionRecordDO.setReqText(content);
        // 生成问题ID：时间戳 + userId 简单拼接 保证完全唯一
        //雪花算法 生成
        long questionId = IdWorker.getId();
        questionRecordDO.setQuestionId(questionId);
        questionRecordDO.setIfDelete(false);
        questionRecordDO.setCreateBy(userId);
        questionRecordDO.setCreateTime(new Date());
        // 记录请求体参数（这里简单封装成JSON，可扩展 AiChatRequest）
        questionRecordDO.setReqParam(requestParam);
        // TODO 初始状态：待响应 3=处理中 还是为null?
//        questionRecordDO.setStatus(3);
        this.save(questionRecordDO);
        return questionId;
    }
//jsonNode: {"id":"684d6258-e183-4e3c-a83f-39dd97f7a14b",
// "object":"chat.completion.chunk",
// "created":1762325860,"model":"deepseek-chat",
// "system_fingerprint":"fp_ffc7281d48_prod0820_fp8_kvcache",
// "choices":[{"index":0,"delta":{"content":""},"logprobs":null,"finish_reason":"stop"}],
// "usage":{"prompt_tokens":15,"completion_tokens":456,"total_tokens":471,
// "prompt_tokens_details":{"cached_tokens":0},"prompt_cache_hit_tokens":0,"prompt_cache_miss_tokens":15}}

    public void saveResponseRecord(Long questionId, JsonNode jsonNode, Integer status, String remark) {
        QuestionRecordDO responseRecord = new QuestionRecordDO();

        responseRecord.setType(QuestionRecordDO.TYPE_RESPONSE);
        LambdaQueryWrapper<QuestionRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRecordDO::getType, QuestionRecordDO.TYPE_QUESTION);
        wrapper.eq(QuestionRecordDO::getQuestionId, questionId);
        QuestionRecordDO question = this.getOne(wrapper);
        Long userId = question.getUserId();
        responseRecord.setUserId(userId);
        responseRecord.setSessionId(question.getSessionId());
        responseRecord.setQuestionId(questionId);

        // 1. 从缓存获取完整回答
        StringBuilder answerBuilder = DeepSeekClient.aiResponseCache.get(questionId);
        String fullAnswer = answerBuilder != null ? answerBuilder.toString() : "";
    // 2. 从jsonNode获取完整回答
        if(jsonNode!=null){
            String responseId = getTextOrNull(jsonNode, "id");
            responseRecord.setResponseId(responseId);
            String respResult= getAllResponseContent(fullAnswer, (ObjectNode) jsonNode);
            responseRecord.setRespResult(respResult);
            //处理token用量（此时jsonNode已被修改，可正常获取其他字段）
            if(jsonNode.get("usage")!=null){
                int totalTokens = jsonNode.get("usage").get("total_tokens").asInt();
                responseRecord.setTokensUsed(totalTokens);
            }

        }
        responseRecord.setReqText(question.getReqText());
        responseRecord.setRespText(fullAnswer);
        responseRecord.setStatus(status);
        responseRecord.setRemark(remark);
        responseRecord.setReqParam(question.getReqParam());
        responseRecord.setCreateTime(new Date());
        responseRecord.setCreateBy(userId);
        responseRecord.setIfDelete(false);
        // 最终落库
        this.save(responseRecord);
        if(status==STATUS_SUCCESS){
            saveToSessionCache(fullAnswer, userId);
        }
    }

    /**
     *  保存 用户提问模板问题-模板答案记录
     * @param questionId
     * @param answer
     * @param status
     * @param remark
     */
    public  void saveQuestionTemplateAnswer(Long questionId, String answer, Integer status, String remark){
        QuestionRecordDO responseRecord = new QuestionRecordDO();

        responseRecord.setType(QuestionRecordDO.TYPE_RESPONSE);
        LambdaQueryWrapper<QuestionRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRecordDO::getType, QuestionRecordDO.TYPE_QUESTION);
        wrapper.eq(QuestionRecordDO::getQuestionId, questionId);
        QuestionRecordDO question = this.getOne(wrapper);
        Long userId = question.getUserId();
        responseRecord.setUserId(userId);
        responseRecord.setSessionId(question.getSessionId());
        responseRecord.setQuestionId(questionId);
        responseRecord.setTokensUsed(0);//模板答案不计算token
        responseRecord.setReqText(question.getReqText());
        responseRecord.setRespText(answer);
        responseRecord.setStatus(status);
        responseRecord.setRemark(remark);
        responseRecord.setReqParam(question.getReqParam());
        responseRecord.setCreateTime(new Date());
        responseRecord.setCreateBy(userId);
        responseRecord.setIfDelete(false);
        // 最终落库
        this.save(responseRecord);
    }
    /**
     * 把ai的回复写入会话缓存
     * @param fullAnswer 完整回答
     * @param userId 用户id
     */
    private void saveToSessionCache(String fullAnswer, Long userId) {
        //落库完把ai回复写进缓存
        AiChatMessage aiChatMessage = new AiChatMessage();
        aiChatMessage.setRole(AiChatMessage.ROLE_ASSISTANT);
        aiChatMessage.setContent(fullAnswer);
        chatHistoryCache.appendMessage(userId, aiChatMessage);
    }

    private String getAllResponseContent(String fullAnswer,ObjectNode jsonNode) {


        // 2. 将不可修改的JsonNode转换为可修改的ObjectNode

        // 3. 逐级获取choices[0].delta节点，并修改content字段
        if (jsonNode.has("choices") && jsonNode.get("choices").isArray()
                && !jsonNode.get("choices").isEmpty()) {

            // 获取choices数组第一个元素（转换为可修改的ObjectNode）
            ObjectNode choiceNode = (ObjectNode) jsonNode.get("choices").get(0);

            // 获取delta节点（若不存在则创建，避免空指针）
            ObjectNode deltaNode = choiceNode.has("delta") ?
                    (ObjectNode) choiceNode.get("delta") :
                    choiceNode.putObject("delta");

            // 将完整回答设置到delta.content中
            deltaNode.put("content", fullAnswer);
        }
        return jsonNode.toString();
    }

    private static String getTextOrNull(JsonNode node, String field) {
        if (node == null || !node.has(field)) return null;
        JsonNode n = node.get(field);
        return (n == null || n.isNull()) ? null : n.asText();
    }

    public List<QuestionRecordVO> getDayChatDetails(Long userId, String day) {
        return questionRecordMapper.queryDayDetails(userId, day);
    }
}


