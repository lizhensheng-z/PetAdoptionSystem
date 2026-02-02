package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 地理编码请求DTO
 */
@Data
@Schema(description = "地理编码请求")
public class GeocodeRequest {
    
    @Schema(description = "地址")
    @NotBlank(message = "地址不能为空")
    private String address;
    
    @Schema(description = "城市")
    private String city;
}