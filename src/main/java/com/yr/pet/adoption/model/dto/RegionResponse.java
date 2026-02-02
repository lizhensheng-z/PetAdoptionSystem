package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 地区响应DTO
 */
@Data
@Schema(description = "地区响应")
public class RegionResponse {
    
    @Schema(description = "地区列表")
    private List<Region> regions;
    
    /**
     * 地区信息
     */
    @Data
    @Schema(description = "地区信息")
    public static class Region {
        private String code;
        private String name;
        private Integer level;
        private String parentCode;
    }
}