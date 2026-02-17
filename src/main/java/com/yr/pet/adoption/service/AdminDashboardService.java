package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.AdminDashboardChartsResponse;
import com.yr.pet.adoption.model.dto.AdminDashboardStatsResponse;
import com.yr.pet.adoption.model.dto.PendingOrgResponse;

import java.util.List;

/**
 * 管理后台Dashboard服务接口
 * @author 宗平
 * @since 2026-02-17
 */
public interface AdminDashboardService {

    /**
     * 获取管理后台核心指标统计数据
     * @return 统计数据
     */
    AdminDashboardStatsResponse getDashboardStats();

    /**
     * 获取管理后台图表数据
     * @param range 时间范围（7days, 30days, 90days）
     * @return 图表数据
     */
    AdminDashboardChartsResponse getDashboardCharts(String range);

    /**
     * 获取待审核机构列表
     * @param limit 返回数量限制
     * @return 待审核机构列表
     */
    List<PendingOrgResponse> getPendingOrganizations(Integer limit);

    /**
     * 审核机构
     * @param userId 机构用户ID
     * @param action 审核动作（approve/reject）
     * @param reason 审核原因
     */
    void auditOrganization(Long userId, String action, String reason);

    /**
     * 获取公告摘要信息
     * @return 公告摘要
     */
    Object getNoticeSummary();
}