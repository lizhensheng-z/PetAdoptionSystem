package com.yr.pet.ai.controller;

import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.ai.model.vo.DaySessionVO;
import com.yr.pet.ai.model.vo.QuestionRecordVO;
import com.yr.pet.ai.service.QuestionRecordService;
import com.yr.pet.ai.service.SessionService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


//@Api(tags = "AI会话服务")
@RestController
@RequestMapping("/ai/session")
public class SessionController {
    @Resource
    private SessionService sessionService;
    @Resource
    private QuestionRecordService questionRecordService;
    /**
     * 按天分组获取会话列表
     */
//    @ApiOperation("按天分组获取会话列表")
    @GetMapping("/listGroupByDay")
    public List<DaySessionVO> listGroupByDay(@RequestParam(defaultValue = "7", required = false) @Max(30) Integer days){
        return sessionService.queryRecentDays(days);

    }
    /**
     * 删除会话
     */
//    @ApiOperation("删除会话")
    @PostMapping("/delete")
    public void delete(@RequestParam("sessionId") Long sessionId) {
        sessionService.deleteSession(sessionId);

    }
    /**
     * 删除当天会话
     */
//    @ApiOperation("删除某天所有会话")
    @PostMapping("/delete/day")
    public void delete(@RequestParam Date date) {
        sessionService.deleteDaySession(date);
    }
    /**
     * 获取会话详情
     */
//    @ApiOperation("获取会话详情")
    @PostMapping("/detail")
    public List<QuestionRecordVO> detail(@RequestParam("sessionId") Long sessionId) {
        return sessionService.getSessionDetail(sessionId);
    }
//    @ApiOperation("查询当天所有会话内容详情")
    @GetMapping("/chat/history/day")
    public List<QuestionRecordVO> getDayChatHistory(@RequestParam String day) {  // yyyy-MM-dd) {
        Long userId = UserContext.getUserId();
        if (userId != null){
            return questionRecordService.getDayChatDetails(userId, day);
        }
        return List.of(); // Return an empty list if userId is null
    }


   
}
