package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码验证请求DTO
 */
@Data
@Schema(description = "验证码验证请求")
public class VerifyCodeRequest {
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String code;
}