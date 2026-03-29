package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 宠物审核请求DTO
 * @author yr
 * @since 2026-02-01
 */
@Data
@Schema(description = "宠物审核请求")
public class PetAuditRequest implements Serializable {

    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Long petId;

    @NotBlank(message = "审核动作不能为空")
    @Schema(description = "审核动作：approve-通过，reject-拒绝", example = "approve")
    private String action;

    @Schema(description = "审核备注/驳回原因", example = "资料完整，符合发布要求")
    private String remark;
}