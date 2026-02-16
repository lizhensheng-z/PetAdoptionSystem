package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 标签创建请求DTO
 * @author yr
 * @since 2026-02-16
 */
@Data
@Schema(description = "标签创建请求")
public class TagCreateRequest {
    
    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称", example = "温顺")
    private String name;
    
    @NotBlank(message = "标签类型不能为空")
    @Schema(description = "标签类型：PERSONALITY/HEALTH/FEATURE", example = "PERSONALITY")
    private String tagType;
    
    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用：0/1", example = "1")
    private Boolean enabled;
}