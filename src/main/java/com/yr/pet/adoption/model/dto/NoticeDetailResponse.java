package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告详情响应DTO
 */
@Data
@Schema(description = "公告详情响应")
public class NoticeDetailResponse {
    
    @Schema(description = "公告ID")
    private Long id;
    
    @Schema(description = "公告标题")
    private String title;
    
    @Schema(description = "公告内容")
    private String content;
    
    @Schema(description = "公告状态")
    private String status;
    
    @Schema(description = "状态文本描述")
    private String statusText;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}