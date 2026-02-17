package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.AdminDashboardChartsResponse;
import com.yr.pet.adoption.model.dto.AdminDashboardStatsResponse;
import com.yr.pet.adoption.model.dto.OrgAuditRequest;
import com.yr.pet.adoption.model.dto.PendingOrgResponse;
import com.yr.pet.adoption.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台Dashboard控制器
 * 提供管理后台首页相关的统计数据、图表数据、机构审核等功能
 * @author 宗平
 * @since 2026-02-17
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理后台Dashboard", description = "管理后台首页相关接口")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 获取管理后台核心指标统计数据
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAuthority('admin:dashboard:view')")
    @Operation(summary = "获取核心指标统计", description = "获取管理后台首页的核心统计数据，包括用户数、机构数、宠物数、领养数等")
    public R<AdminDashboardStatsResponse> getDashboardStats() {
        AdminDashboardStatsResponse stats = adminDashboardService.getDashboardStats();
        return R.ok(stats);
    }

    /**
     * 获取管理后台图表数据
     */
    @GetMapping("/dashboard/charts")
    @PreAuthorize("hasAuthority('admin:dashboard:view')")
    @Operation(summary = "获取图表数据", description = "获取管理后台首页的图表数据，包括趋势图、饼图等")
    public R<AdminDashboardChartsResponse> getDashboardCharts(
            @Parameter(description = "时间范围", example = "7days") 
            @RequestParam(defaultValue = "7days") String range) {
        
        AdminDashboardChartsResponse charts = adminDashboardService.getDashboardCharts(range);
        return R.ok(charts);
    }

    /**
     * 获取待审核机构列表
     */
    @GetMapping("/organizations/pending")
    @PreAuthorize("hasAuthority('admin:organization:audit')")
    @Operation(summary = "获取待审核机构", description = "获取待审核的机构列表，用于管理后台首页展示")
    public R<List<PendingOrgResponse>> getPendingOrganizations(
            @Parameter(description = "返回数量限制", example = "5") 
            @RequestParam(defaultValue = "5") Integer limit) {
        
        List<PendingOrgResponse> pendingOrgs = adminDashboardService.getPendingOrganizations(limit);
        return R.ok(pendingOrgs);
    }

    /**
     * 审核机构
     */
    @PutMapping("/organizations/{id}/audit")
    @PreAuthorize("hasAuthority('admin:organization:audit')")
    @Operation(summary = "审核机构", description = "对机构进行审核操作，支持通过或拒绝")
    public R<Void> auditOrganization(
            @Parameter(description = "机构用户ID", example = "123") 
            @PathVariable Long id,
            @Valid @RequestBody OrgAuditRequest request) {
        
        adminDashboardService.auditOrganization(id, request.getAction(), request.getReason());
        return R.ok();
    }

    /**
     * 获取公告摘要信息
     */
    @GetMapping("/notices/summary")
    @PreAuthorize("hasAuthority('admin:notice:view')")
    @Operation(summary = "获取公告摘要", description = "获取最新的公告摘要信息，用于管理后台首页展示")
    public R<Object> getNoticeSummary() {
        Object summary = adminDashboardService.getNoticeSummary();
        return R.ok(summary);
    }
}