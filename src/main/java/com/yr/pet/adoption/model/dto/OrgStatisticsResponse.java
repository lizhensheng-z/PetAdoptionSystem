package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构统计数据响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "机构统计数据响应")
public class OrgStatisticsResponse {

    @Schema(description = "在养宠物总数", example = "25")
    private Integer totalPets;

    @Schema(description = "待处理申请数", example = "8")
    private Integer pendingApplications;

    @Schema(description = "本月领养数", example = "12")
    private Integer monthlyAdoptions;

    @Schema(description = "待回访数", example = "3")
    private Integer pendingFollowups;

    @Schema(description = "累计领养数", example = "156")
    private Integer totalAdoptions;

    @Schema(description = "已发布宠物数", example = "18")
    private Integer publishedPets;

    @Schema(description = "已领养宠物数", example = "10")
    private Integer adoptedPets;

    @Schema(description = "草稿宠物数（已废弃）", example = "0")
    @Deprecated
    private Integer draftPets;

    @Schema(description = "审核中宠物数（已废弃）", example = "0")
    @Deprecated
    private Integer underReviewPets;

    @Schema(description = "猫咪数量")
    private Integer catCount;

    @Schema(description = "狗狗数量")
    private Integer dogCount;

    @Schema(description = "其他宠物数量")
    private Integer otherCount;

    @Schema(description = "近半年领养趋势")
    private List<MonthlyAdoption> adoptionTrend;

    @Schema(description = "响应时间戳")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 月度领养数据
     */
    @Data
    public static class MonthlyAdoption {
        private String month;
        private Integer count;
    }
}