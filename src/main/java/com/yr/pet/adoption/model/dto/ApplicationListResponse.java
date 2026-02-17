package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 申请列表响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "申请列表响应")
public class ApplicationListResponse {
    
    @Schema(description = "申请ID", example = "1001")
    private Long id;
    
    @Schema(description = "宠物ID", example = "789")
    private Long petId;
    
    @Schema(description = "宠物名称", example = "小白")
    private String petName;
    
    @Schema(description = "宠物封面图片URL")
    private String petCoverUrl;
    
    @Schema(description = "申请人ID", example = "456")
    private Long userId;
    
    @Schema(description = "申请人姓名", example = "张三")
    private String userName;
    
    @Schema(description = "申请人头像URL")
    private String userAvatar;
    
    @Schema(description = "申请状态", example = "SUBMITTED")
    private String status;
    
    @Schema(description = "状态描述", example = "已提交")
    private String statusDesc;
    
    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    
    @Schema(description = "问卷答案")
    private Map<String, String> questionnaire;
}