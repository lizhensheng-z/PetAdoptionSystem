package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户详细信息响应（含信用分、统计数据）")
public class UserDetailResponse {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "用户角色")
    private String role;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "信用分")
    private Integer creditScore;

    @Schema(description = "信用分变化")
    private Integer creditChange;

    @Schema(description = "信用等级")
    private String creditLevel;

    @Schema(description = "徽章列表")
    private List<String> medals;

    @Schema(description = "统计数据")
    private UserStatsResponse stats;

    @Schema(description = "创建时间")
    private String createTime;
}