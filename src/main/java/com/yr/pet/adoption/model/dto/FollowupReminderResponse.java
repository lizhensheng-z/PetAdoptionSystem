package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回访提醒响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "回访提醒响应")
public class FollowupReminderResponse {
    
    @Schema(description = "回访提醒ID", example = "1001")
    private Long id;
    
    @Schema(description = "宠物ID", example = "791")
    private Long petId;
    
    @Schema(description = "宠物名称", example = "小花")
    private String petName;
    
    @Schema(description = "宠物封面图片URL")
    private String petCoverUrl;
    
    @Schema(description = "领养申请ID", example = "1001")
    private Long adoptionApplicationId;
    
    @Schema(description = "领养时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime adoptedTime;
    
    @Schema(description = "领养人ID", example = "458")
    private Long userId;
    
    @Schema(description = "领养人姓名", example = "王五")
    private String userName;
    
    @Schema(description = "领养人手机号", example = "13900139000")
    private String userPhone;
    
    @Schema(description = "上次回访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastFollowupTime;
    
    @Schema(description = "下次回访日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime nextFollowupDate;
    
    @Schema(description = "超期天数", example = "19")
    private Integer overdueDays;
    
    @Schema(description = "回访状态：overdue/soon", example = "overdue")
    private String status;
}