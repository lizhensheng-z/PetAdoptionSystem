package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 公告更新请求DTO
 */
@Data
@Schema(description = "公告更新请求")
public class NoticeUpdateRequest {
    
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 2, max = 128, message = "公告标题长度必须在2-128个字符之间")
    @Schema(description = "公告标题", example = "系统维护通知")
    private String title;
    
    @NotBlank(message = "公告内容不能为空")
    @Size(min = 10, max = 2000, message = "公告内容长度必须在10-2000个字符之间")
    @Schema(description = "公告内容", example = "系统将于今晚22:00-23:00进行维护...")
    private String content;
    
    @Schema(description = "公告状态：DRAFT/PUBLISHED/REMOVED", example = "PUBLISHED")
    private String status;
}