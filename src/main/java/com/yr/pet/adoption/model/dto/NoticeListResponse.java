package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告列表项响应DTO
 */
@Data
@Schema(description = "公告列表项响应")
public class NoticeListResponse {
    
    @Schema(description = "公告ID")
    private Long id;
    
    @Schema(description = "公告标题")
    private String title;
    
    @Schema(description = "公告内容摘要")
    private String contentSummary;
    
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
    
    @Schema(description = "是否为新公告（7天内创建）")
    private Boolean isNew;
}