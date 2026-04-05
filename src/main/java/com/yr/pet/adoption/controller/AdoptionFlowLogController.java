package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.service.AdoptionFlowLogService;
import com.yr.pet.adoption.model.vo.ApplicationFlowLogVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领养申请流程日志控制器
 * @author yr
 * @since 2026-01-01
 */
@RestController
@Tag(name = "领养申请流程日志", description = "领养申请流程日志相关接口")
public class AdoptionFlowLogController {

    @Autowired
    private AdoptionFlowLogService adoptionFlowLogService;

    /**
     * 获取申请流程日志
     */
    @GetMapping("/api/adoption/applications/{applicationId}/logs")
    @Operation(summary = "获取申请流程日志", description = "获取指定申请的所有流程日志")
    public R<List<ApplicationFlowLogVO>> getApplicationLogs(@PathVariable Long applicationId) {
        List<ApplicationFlowLogVO> logs = adoptionFlowLogService.getApplicationLogs(applicationId);
        return R.ok(logs);
    }

    /**
     * 机构获取申请流程日志
     */
    @GetMapping("/api/org/adoption/applications/{applicationId}/logs")
    @Operation(summary = "机构获取申请流程日志", description = "机构获取指定申请的所有流程日志")
    public R<List<ApplicationFlowLogVO>> getOrgApplicationLogs(@PathVariable Long applicationId) {
        List<ApplicationFlowLogVO> logs = adoptionFlowLogService.getApplicationLogs(applicationId);
        return R.ok(logs);
    }
}