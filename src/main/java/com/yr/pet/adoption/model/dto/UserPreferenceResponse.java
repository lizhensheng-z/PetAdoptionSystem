package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户偏好设置响应")
public class UserPreferenceResponse {

    @Schema(description = "宠物类型偏好")
    private List<String> petTypes;

    @Schema(description = "年龄范围（月）[最小, 最大]")
    private List<Integer> ageRange;

    @Schema(description = "性别偏好")
    private String gender;

    @Schema(description = "标签偏好")
    private List<String> tags;

    @Schema(description = "距离偏好（公里）")
    private Integer distance;

    @Schema(description = "体型偏好")
    private List<String> sizes;

    @Schema(description = "健康要求")
    private List<String> healthRequirements;
}