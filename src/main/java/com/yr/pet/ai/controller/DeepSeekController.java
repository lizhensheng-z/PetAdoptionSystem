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
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai/deepseek")
@Slf4j
public class DeepSeekController {

    @Resource
    private DeepSeekClient deepSeekClient;

    @Resource
    private SessionService sessionService;
    @Resource
    private QuestionTemplateService questionTemplateService;

//    @ApiOperation("AI聊天问答-流式响应")
    @GetMapping(value = "/chatCompletions", produces = "text/event-stream;charset=utf-8")
    public Flux<ServerSentEvent<String>> chatCompletions(@NotNull @RequestParam(value = "content") String content) {


        //先创建会话 无论是否命中模板
        String title = content.length() > 10 ? content.substring(0,10) : content;
        SessionDO sessionDO = sessionService.createSession(title);
        //先查询本地模板库是否有匹配的问答 命中则直接返回 并扣减用户次数
        String respone = questionTemplateService.selectByQuestion(content,sessionDO.getSessionId());
        if(respone != null){
            ServerSentEvent<String> event = ServerSentEvent.<String>builder()
                    .event("message")
                    .data(respone)
                    .build();
            return Flux.just(event);
        }
        //再调用 三方ai接口
        Flux<ServerSentEvent<String>> result = deepSeekClient.chatCompletions(content,sessionDO);
        return result;
    }


    @GetMapping("/account/balance")
    public BalanceVO getBalance() {
        return deepSeekClient.getBalance();
    }

}
