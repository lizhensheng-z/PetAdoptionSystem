package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 公告状态更新请求DTO
 */
@Data
@Schema(description = "公告状态更新请求")
public class NoticeStatusUpdateRequest {
    
    @NotBlank(message = "公告状态不能为空")
    @Schema(description = "公告状态：DRAFT/PUBLISHED/REMOVED", example = "PUBLISHED")
    private String status;
}