package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信用摘要响应DTO
 * 用于返回用户信用账户的摘要信息
 * 
 * @author 宗平
 * @since 2024-02-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户信用摘要响应")
public class UserCreditSummaryResponse {

    @Schema(description = "当前信用分数", example = "850")
    private Integer score;

    @Schema(description = "当前等级名称", example = "资深领养人")
    private String levelName;

    @Schema(description = "等级图标标识", example = "shield-check")
    private String levelIcon;

    @Schema(description = "等级颜色代码", example = "#FF8C42")
    private String levelColor;

    @Schema(description = "总打卡天数", example = "45")
    private Integer totalCheckins;

    @Schema(description = "连续打卡天数", example = "7")
    private Integer consecutiveCheckins;

    @Schema(description = "下一等级名称", example = "金牌领养人")
    private String nextLevelName;

    @Schema(description = "下一等级所需分数", example = "1000")
    private Integer nextLevelThreshold;

    @Schema(description = "到下一等级的进度百分比", example = "85")
    private Integer nextLevelProgress;

    @Schema(description = "排名百分比", example = "Top 5%")
    private String ranking;

    @Schema(description = "总用户数", example = "1250")
    private Integer totalUsers;

    @Schema(description = "当前排名", example = "63")
    private Integer rank;

    @Schema(description = "最近变动分数", example = "15")
    private Integer recentChange;

    @Schema(description = "最近变动原因", example = "连续7天打卡")
    private String recentChangeReason;

    @Schema(description = "最后打卡日期", example = "2026-02-17")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckinDate;

    @Schema(description = "账户创建时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间", example = "2026-02-17 20:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public UserCreditSummaryResponse(Integer score, String levelName, String levelIcon, int totalCheckins, int nextLevelThreshold, String ranking) {
        this.score = score;
        this.levelName = levelName;
        this.levelIcon = levelIcon;
        this.totalCheckins = totalCheckins;
        this.nextLevelThreshold = nextLevelThreshold;
        this.ranking = ranking;
    }
}