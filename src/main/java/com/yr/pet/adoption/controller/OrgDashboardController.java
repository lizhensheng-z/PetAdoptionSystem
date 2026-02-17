package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.OrgDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 机构管理首页控制器
 * @author 宗平
 * @since 2026-02-17
 */
@RestController
@RequestMapping("/api/org")
@Tag(name = "机构管理首页", description = "机构管理首页相关接口")
public class OrgDashboardController {

    @Autowired
    private OrgDashboardService orgDashboardService;

    /**
     * 获取机构统计数据
     */
    @GetMapping("/dashboard/statistics")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取机构统计数据", description = "获取当前机构的关键统计数据")
    public R<OrgStatisticsResponse> getStatistics() {
        Long orgUserId = UserContext.getUserId();
        OrgStatisticsResponse statistics = orgDashboardService.getStatistics(orgUserId);
        return R.ok(statistics);
    }

    /**
     * 获取待办事项列表
     */
    @GetMapping("/dashboard/todos")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取待办事项列表", description = "获取当前机构的待办事项")
    public R<TodoListResponse> getTodos(
            @Parameter(description = "待办类型筛选：application/followup/audit") 
            @RequestParam(required = false) String type,
            
            @Parameter(description = "返回数量限制", example = "10") 
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Long orgUserId = UserContext.getUserId();
        TodoListResponse todos = orgDashboardService.getTodos(orgUserId, type, limit);
        return R.ok(todos);
    }

    /**
     * 获取最近宠物列表
     */
    @GetMapping("/pets/recent")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取最近宠物列表", description = "获取机构最近发布的宠物列表")
    public R<PetListWrapperResponse> getRecentPets(
            @Parameter(description = "返回数量限制", example = "5") 
            @RequestParam(defaultValue = "5") Integer limit) {
        
        Long orgUserId = UserContext.getUserId();
        PetListWrapperResponse response = new PetListWrapperResponse();
        response.setList(orgDashboardService.getRecentPets(orgUserId, limit));
        return R.ok(response);
    }

    /**
     * 获取最新申请列表
     */
    @GetMapping("/applications/recent")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取最新申请列表", description = "获取机构最新的领养申请列表")
    public R<ApplicationListWrapperResponse> getRecentApplications(
            @Parameter(description = "返回数量限制", example = "5") 
            @RequestParam(defaultValue = "5") Integer limit) {
        
        Long orgUserId = UserContext.getUserId();
        ApplicationListWrapperResponse response = new ApplicationListWrapperResponse();
        response.setList(orgDashboardService.getRecentApplications(orgUserId, limit));
        return R.ok(response);
    }

    /**
     * 获取回访提醒列表
     */
    @GetMapping("/followup/reminders")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取回访提醒列表", description = "获取需要回访的宠物列表")
    public R<FollowupReminderListResponse> getFollowupReminders(
            @Parameter(description = "回访状态：overdue/soon/all", example = "all") 
            @RequestParam(defaultValue = "all") String status,
            
            @Parameter(description = "返回数量限制", example = "10") 
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Long orgUserId = UserContext.getUserId();
        FollowupReminderListResponse reminders = orgDashboardService.getFollowupReminders(orgUserId, status, limit);
        return R.ok(reminders);
    }

    /**
     * 获取首页综合数据（推荐接口）
     */
    @GetMapping("/dashboard/home")
    @PreAuthorize("hasRole('ORG')")
    @Operation(summary = "获取首页综合数据", description = "一次性获取首页所需的所有数据，减少请求次数")
    public R<OrgDashboardHomeResponse> getHomeData() {
        Long orgUserId = UserContext.getUserId();
        OrgDashboardHomeResponse homeData = orgDashboardService.getHomeData(orgUserId);
        return R.ok(homeData);
    }
}