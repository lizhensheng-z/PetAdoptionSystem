package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 地理编码响应DTO
 */
@Data
@Schema(description = "地理编码响应")
public class GeocodeResponse {
    
    @Schema(description = "地址")
    private String address;
    
    @Schema(description = "经度")
    private Double lng;
    
    @Schema(description = "纬度")
    private Double lat;
    
    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "城市")
    private String city;
    
    @Schema(description = "区县")
    private String district;
    
    @Schema(description = "精度")
    private String precision;
}