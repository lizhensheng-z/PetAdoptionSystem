package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 验证码响应DTO
 */
@Data
@Schema(description = "验证码响应")
public class CaptchaResponse {
    
    @Schema(description = "验证码ID")
    private String captchaId;
    
    @Schema(description = "验证码图片(base64)")
    private String image;
    
    @Schema(description = "有效期(秒)")
    private Integer expiresIn;
}