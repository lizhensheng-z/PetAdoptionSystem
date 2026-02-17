package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

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
    
    @Schema(description = "草稿宠物数", example = "7")
    private Integer draftPets;
    
    @Schema(description = "审核中宠物数", example = "2")
    private Integer underReviewPets;
    
    @Schema(description = "响应时间戳")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}