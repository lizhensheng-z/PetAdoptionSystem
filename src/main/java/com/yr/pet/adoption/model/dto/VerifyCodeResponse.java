package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 验证码验证响应DTO
 */
@Data
@Schema(description = "验证码验证响应")
public class VerifyCodeResponse {
    
    @Schema(description = "临时令牌")
    private String token;
    
    @Schema(description = "有效期(秒)")
    private Integer expiresIn;
}