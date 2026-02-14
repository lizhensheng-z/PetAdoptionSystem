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
    
    @NotBlank(message = "申请理由不能为空")
    @Size(max = 500, message = "申请理由不能超过500字")
    @Schema(description = "申请理由", example = "我非常喜欢这只猫咪，有多年养猫经验，能提供良好的生活环境")
    private String reason;
    
    @NotBlank(message = "联系方式不能为空")
    @Schema(description = "联系方式", example = "13800138000")
    private String contactInfo;
    
    @Size(max = 200, message = "备注信息不能超过200字")
    @Schema(description = "备注信息", example = "周末时间充裕，可随时上门看猫")
    private String remarks;
    
    @Schema(description = "领养问卷数据", example = "{\"housing\":\"自有住房\",\"experience\":\"有3年养猫经验\"}")
    private Map<String, Object> questionnaire;
}