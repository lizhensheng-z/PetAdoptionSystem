package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.common.R;

import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.vo.*;
import com.yr.pet.adoption.service.AdoptionApplicationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 领养申请控制器
 * @author yr
 * @since 2026-01-01
 */
@RestController
@Tag(name = "领养申请管理", description = "领养申请相关接口")
public class AdoptionApplicationController {

    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    /**
     * 用户提交领养申请
     */
    @PostMapping("/api/adoption/applications")
    @Operation(summary = "提交领养申请", description = "用户提交新的领养申请")
    public R<AdoptionApplicationResponse> createApplication(@Valid @RequestBody AdoptionApplicationRequest request) {
        Long userId = UserContext.getUserId();
        AdoptionApplicationResponse response = adoptionApplicationService.createApplication(userId, request);
        return R.ok(response);
    }

    /**
     * 用户查看我的申请列表
     */
    @GetMapping("/api/adoption/applications")
    @Operation(summary = "获取我的申请列表", description = "获取当前用户的所有领养申请列表")
    public R<IPage<MyApplicationVO>> getMyApplications(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "create_time") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        Long userId = UserContext.getUserId();
        IPage<MyApplicationVO> page = adoptionApplicationService.getMyApplications(userId, status, pageNo, pageSize, sortBy, order);
        return R.ok(page);
    }

    /**
     * 用户查看申请详情
     */
    @GetMapping("/api/adoption/applications/{applicationId}")
    @Operation(summary = "获取申请详情", description = "获取指定申请的详细信息")
    public R<ApplicationDetailVO> getApplicationDetail(@PathVariable Long applicationId) {
        Long userId = UserContext.getUserId();
        ApplicationDetailVO vo = adoptionApplicationService.getApplicationDetail(userId, applicationId);
        return R.ok(vo);
    }

    /**
     * 用户取消申请
     */
    @PostMapping("/api/adoption/applications/{applicationId}/cancel")
    @Operation(summary = "取消申请", description = "用户取消进行中的领养申请")
    public R<Void> cancelApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationCancelRequest request) {
        
        Long userId = UserContext.getUserId();
        adoptionApplicationService.cancelApplication(userId, applicationId, request);
        return R.ok();
    }

    /**
     * 机构查看申请列表
     */
    @GetMapping("/api/org/adoption/applications")
    @Operation(summary = "机构获取申请列表", description = "机构获取其发布宠物的所有领养申请列表")
    public R<IPage<OrgApplicationVO>> getOrgApplications(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "create_time") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        Long orgUserId = UserContext.getUserId();
        IPage<OrgApplicationVO> page = adoptionApplicationService.getOrgApplications(
                orgUserId, petId, status, keyword, pageNo, pageSize, sortBy, order);
        return R.ok(page);
    }

    /**
     * 机构查看申请详情
     */
    @GetMapping("/api/org/adoption/applications/{applicationId}")
    @Operation(summary = "机构获取申请详情", description = "机构获取指定申请的详细信息")
    public R<ApplicationDetailVO> getOrgApplicationDetail(@PathVariable Long applicationId) {
        Long orgUserId = UserContext.getUserId();
        ApplicationDetailVO vo = adoptionApplicationService.getOrgApplicationDetail(orgUserId, applicationId);
        return R.ok(vo);
    }

    /**
     * 机构更新申请状态
     */
    @PostMapping("/api/org/adoption/applications/{applicationId}/status")
    @Operation(summary = "更新申请状态", description = "机构更新领养申请的处理状态")
    public R<StatusUpdateResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody StatusUpdateRequest request) {
        
        Long orgUserId = UserContext.getUserId();
        StatusUpdateResponse response = adoptionApplicationService.updateApplicationStatus(orgUserId, applicationId, request);
        return R.ok(response);
    }
}