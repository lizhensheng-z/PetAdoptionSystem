package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送验证码请求DTO
 */
@Data
@Schema(description = "发送验证码请求")
public class SendCodeRequest {
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "用途：register、reset_password、bind_phone、verify_email")
    @NotBlank(message = "用途不能为空")
    private String type;
    
    @Schema(description = "验证码ID")
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    @Schema(description = "验证码答案")
    @NotBlank(message = "验证码答案不能为空")
    private String captchaAnswer;
}