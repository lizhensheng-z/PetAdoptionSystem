package com.yr.pet.ai.controller;

import com.yr.pet.adoption.common.R;
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
@RequestMapping("/api/ai/session")
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
    public R<List<DaySessionVO>> listGroupByDay(@RequestParam(defaultValue = "7", required = false) @Max(30) Integer days){
        List<DaySessionVO> daySessionVOS = sessionService.queryRecentDays(days);
        return R.ok(daySessionVOS);

    }
    /**
     * 删除会话
     */
//    @ApiOperation("删除会话")
    @PostMapping("/delete")
    public R<Void> delete(@RequestParam("sessionId") Long sessionId) {
        sessionService.deleteSession(sessionId);
        return R.ok();
    }
    /**
     * 删除当天会话
     */
//    @ApiOperation("删除某天所有会话")
    @PostMapping("/delete/day")
    public R<Void> delete(@RequestParam Date date) {
        sessionService.deleteDaySession(date);
        return R.ok();
    }
    /**
     * 获取会话详情
     */
//    @ApiOperation("获取会话详情")
    @PostMapping("/detail")
    public R<List<QuestionRecordVO>> detail(@RequestParam("sessionId") Long sessionId) {
        List<QuestionRecordVO> sessionDetail = sessionService.getSessionDetail(sessionId);
        return R.ok(sessionDetail);
    }
//    @ApiOperation("查询当天所有会话内容详情")
    @GetMapping("/chat/history/day")
    public R<List<QuestionRecordVO>> getDayChatHistory(@RequestParam String day) {  // yyyy-MM-dd) {
        Long userId = UserContext.getUserId();
        if (userId != null){
            List<QuestionRecordVO> dayChatDetails = questionRecordService.getDayChatDetails(userId, day);
            return R.ok(dayChatDetails);
        }
        return R.ok(List.of());// Return an empty list if userId is null
    }


   
}
