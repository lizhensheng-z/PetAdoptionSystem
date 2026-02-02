package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 刷新token请求DTO
 */
@Data
@Schema(description = "刷新token请求")
public class RefreshTokenRequest {
    
    @Schema(description = "刷新令牌", example = "eyJhbGc...")
    private String refreshToken;
}