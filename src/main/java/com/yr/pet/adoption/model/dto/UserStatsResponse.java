package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户统计信息响应")
public class UserStatsResponse {

    @Schema(description = "申请数量")
    private Integer applications;

    @Schema(description = "收藏数量")
    private Integer favorites;

    @Schema(description = "打卡数量")
    private Integer checkins;

    @Schema(description = "领养数量")
    private Integer adoptions;

    @Schema(description = "待处理申请数")
    private Integer pendingApplications;

    @Schema(description = "本月打卡数")
    private Integer monthlyCheckins;
}