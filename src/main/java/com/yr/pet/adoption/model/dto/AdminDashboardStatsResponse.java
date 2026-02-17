package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理后台Dashboard统计数据响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "管理后台Dashboard统计数据响应")
public class AdminDashboardStatsResponse implements Serializable {

    @Schema(description = "总用户数统计")
    private StatItem totalUsers;

    @Schema(description = "机构总数统计")
    private StatItem totalOrgs;

    @Schema(description = "宠物总数统计")
    private StatItem totalPets;

    @Schema(description = "领养总数统计")
    private StatItem totalAdoptions;

    @Schema(description = "待审核机构数")
    private StatItem pendingOrgs;

    @Schema(description = "今日新增用户数")
    private StatItem todayNewUsers;

    @Schema(description = "今日新增宠物数")
    private StatItem todayNewPets;

    @Schema(description = "今日新增申请数")
    private StatItem todayNewApplications;

    /**
     * 统计项
     */
    @Data
    @Schema(description = "统计项")
    public static class StatItem implements Serializable {
        @Schema(description = "数值")
        private Long value;

        @Schema(description = "环比趋势百分比（正数表示增长，负数表示下降）")
        private Integer trend;

        public StatItem() {}

        public StatItem(Long value, Integer trend) {
            this.value = value;
            this.trend = trend;
        }
    }
}