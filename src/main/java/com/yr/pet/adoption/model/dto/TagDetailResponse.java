package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签详情响应DTO
 * @author yr
 * @since 2026-02-16
 */
@Data
@Schema(description = "标签详情响应")
public class TagDetailResponse {
    
    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String name;
    
    @Schema(description = "标签类型：PERSONALITY/HEALTH/FEATURE")
    private String tagType;
    
    @Schema(description = "是否启用：0/1")
    private Boolean enabled;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}