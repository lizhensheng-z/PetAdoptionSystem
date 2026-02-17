package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "待办事项响应")
public class TodoItemResponse {
    
    @Schema(description = "待办事项ID", example = "1001")
    private Long id;
    
    @Schema(description = "待办类型：application/followup/audit", example = "application")
    private String type;
    
    @Schema(description = "待办标题", example = "张三申请领养\"小白\"")
    private String title;
    
    @Schema(description = "宠物名称（申请类型时）", example = "小白")
    private String petName;
    
    @Schema(description = "宠物ID", example = "789")
    private Long petId;
    
    @Schema(description = "宠物封面图片URL")
    private String petCoverUrl;
    
    @Schema(description = "申请人姓名（申请类型时）", example = "张三")
    private String userName;
    
    @Schema(description = "申请人ID（申请类型时）", example = "456")
    private Long userId;
    
    @Schema(description = "申请人头像URL（申请类型时）")
    private String userAvatar;
    
    @Schema(description = "状态（申请类型时）", example = "SUBMITTED")
    private String status;
    
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    
    @Schema(description = "优先级：high/medium/urgent/low", example = "high")
    private String priority;
    
    @Schema(description = "领养时间（回访类型时）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime adoptionTime;
    
    @Schema(description = "超期天数（回访类型时）", example = "7")
    private Integer overdueDays;
    
    @Schema(description = "上次回访时间（回访类型时）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFollowupTime;
}