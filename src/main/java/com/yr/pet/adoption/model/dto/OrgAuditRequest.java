package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 机构审核请求
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "机构审核请求")
public class OrgAuditRequest implements Serializable {

    @NotBlank(message = "审核动作不能为空")
    @Schema(description = "审核动作：approve-通过，reject-拒绝", example = "approve")
    private String action;

    @Schema(description = "审核原因或备注", example = "资料完整，符合要求")
    private String reason;

    @Schema(description = "是否发送通知给用户")
    private Boolean sendNotification = true;
}