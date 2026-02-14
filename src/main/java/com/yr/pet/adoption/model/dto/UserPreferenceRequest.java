package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户偏好设置请求")
public class UserPreferenceRequest {

    @Schema(description = "宠物类型偏好（cat, dog, other）")
    private List<String> petTypes;

    @Schema(description = "年龄范围（月）[最小, 最大]")
    private List<Integer> ageRange;

    @Schema(description = "性别偏好（MALE, FEMALE, UNKNOWN）")
    private String gender;

    @Schema(description = "标签偏好")
    private List<String> tags;

    @Min(value = 1, message = "距离最小为1公里")
    @Max(value = 500, message = "距离最大为500公里")
    @Schema(description = "距离偏好（公里）")
    private Integer distance;

    @Schema(description = "体型偏好（S, M, L）")
    private List<String> sizes;

    @Schema(description = "健康要求（已绝育, 已疫苗, 已驱虫）")
    private List<String> healthRequirements;
}