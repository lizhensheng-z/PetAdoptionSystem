package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 领养申请请求DTO
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "领养申请请求")
public class AdoptionApplicationRequest {

    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Long petId;

    @Schema(description = "领养问卷数据", example = "{\"housing\":\"自有住房\",\"experience\":\"有3年养猫经验\"}")
    private Map<String, Object> questionnaire;
}