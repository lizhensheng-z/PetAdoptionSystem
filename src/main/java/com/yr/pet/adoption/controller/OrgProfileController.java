package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.OrgProfileService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 机构管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/org")
public class OrgProfileController {

    @Autowired
    private OrgProfileService orgProfileService;
    /**
     * 获取机构资料
     */
    @GetMapping("/profile")
//    @PreAuthorize("hasAuthority('org:profile:read')")
    public R<OrgProfileResponse> getProfile() {
        Long userId = UserContext.getUserId();
        OrgProfileResponse response = orgProfileService.getProfile(userId);
        return R.ok(response);
    }

    /**
     * 更新机构资料
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('org:profile:update')")
    public R<Void> updateProfile(@Validated @RequestBody OrgProfileUpdateRequest request) {
        Long userId = UserContext.getUserId();
        orgProfileService.updateProfile(userId, request);
        return R.ok();
    }

    /**
     * 获取机构统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('org:statistics:read')")
    public R<OrgStatisticsResponse> getStatistics() {
        Long userId = UserContext.getUserId();
        OrgStatisticsResponse response = orgProfileService.getStatistics(userId);
        return R.ok(response);
    }

    /**
     * 获取领养完成记录
     */
    @GetMapping("/adoptions")
    @PreAuthorize("hasAuthority('org:adoptions:read')")
    public R<com.yr.pet.adoption.common.PageResult<OrgAdoptionRecord>> getAdoptionRecords(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "adoptedTime") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        Long currentUserId = UserContext.getUserId();
        com.yr.pet.adoption.common.PageResult<OrgAdoptionRecord> result = 
                orgProfileService.getAdoptionRecords(currentUserId, petId, userId, month, pageNo, pageSize);
        return R.ok(result);
    }

/**
     * 获取回访提醒
     */
    @GetMapping("/followup-reminders")
    @PreAuthorize("hasAuthority('org:followup:read')")
    public R<FollowupReminderResponse> getFollowupReminders() {
        Long userId = UserContext.getUserId();
        FollowupReminderResponse response = orgProfileService.getFollowupReminders(userId);
        return R.ok(response);
    }

    // ==================== 机构首页Dashboard接口 ====================

    /**
     * 获取机构首页统计数据
     */
    @GetMapping("/dashboard/statistics")
    @Operation(summary = "获取机构首页统计数据", description = "获取当前机构的关键统计数据")
    public R<DashboardStatisticsResponse> getDashboardStatistics() {
        Long userId = UserContext.getUserId();
        DashboardStatisticsResponse response = orgProfileService.getDashboardStatistics(userId);
        return R.ok(response);
    }

    /**
     * 获取机构待办事项列表
     */
    @GetMapping("/dashboard/todos")
    @Operation(summary = "获取机构待办事项列表", description = "获取当前机构的待办事项，包括待审核申请、超期回访提醒等")
    public R<TodoListResponse> getDashboardTodos(
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        TodoListResponse response = orgProfileService.getDashboardTodos(userId, type, limit);
        return R.ok(response);
    }

    /**
     * 获取机构最近宠物列表
     */
    @GetMapping("/pets/recent")
    @Operation(summary = "获取机构最近宠物列表", description = "获取机构最近发布的宠物列表，用于首页展示")
    public R<RecentPetListResponse> getRecentPets(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        Long userId = UserContext.getUserId();
        RecentPetListResponse response = orgProfileService.getRecentPets(userId, limit);
        return R.ok(response);
    }

    /**
     * 获取机构最新申请列表
     */
    @GetMapping("/applications/recent")
    @Operation(summary = "获取机构最新申请列表", description = "获取机构最新的领养申请列表，用于首页展示")
    public R<RecentApplicationListResponse> getRecentApplications(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        Long userId = UserContext.getUserId();
        RecentApplicationListResponse response = orgProfileService.getRecentApplications(userId, limit);
        return R.ok(response);
    }

    /**
     * 获取机构回访提醒列表
     */
    @GetMapping("/followup/reminders")
    @Operation(summary = "获取机构回访提醒列表", description = "获取需要回访的宠物列表，包括即将到期和已超期的回访")
    public R<FollowupReminderListResponse> getFollowupReminderList(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        FollowupReminderListResponse response = orgProfileService.getFollowupReminderList(userId, status, limit);
        return R.ok(response);
    }

    /**
     * 获取机构首页综合数据（推荐接口）
     */
    @GetMapping("/dashboard/home")
    @Operation(summary = "获取机构首页综合数据", description = "一次性获取首页所需的所有数据，减少请求次数，提升性能")
    public R<DashboardHomeResponse> getDashboardHome() {
        Long userId = UserContext.getUserId();
        DashboardHomeResponse response = orgProfileService.getDashboardHome(userId);
        return R.ok(response);
    }
}
