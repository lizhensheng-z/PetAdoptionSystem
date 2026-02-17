package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 管理后台Dashboard图表数据响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "管理后台Dashboard图表数据响应")
public class AdminDashboardChartsResponse implements Serializable {

    @Schema(description = "用户活跃度趋势数据")
    private ActivityTrend activityTrend;

    @Schema(description = "领养申请状态分布")
    private List<PieChartItem> adoptionDistribution;

    @Schema(description = "用户注册趋势")
    private ActivityTrend userRegistrationTrend;

    @Schema(description = "宠物发布趋势")
    private ActivityTrend petPublishTrend;

    @Schema(description = "机构认证状态分布")
    private List<PieChartItem> orgVerifyDistribution;

    /**
     * 活跃度趋势数据
     */
    @Data
    @Schema(description = "活跃度趋势数据")
    public static class ActivityTrend implements Serializable {
        @Schema(description = "日期列表")
        private List<String> dates;

        @Schema(description = "数值列表")
        private List<Long> values;
    }

    /**
     * 饼图数据项
     */
    @Data
    @Schema(description = "饼图数据项")
    public static class PieChartItem implements Serializable {
        @Schema(description = "名称")
        private String name;

        @Schema(description = "数值")
        private Long value;

        public PieChartItem() {}

        public PieChartItem(String name, Long value) {
            this.name = name;
            this.value = value;
        }
    }
}