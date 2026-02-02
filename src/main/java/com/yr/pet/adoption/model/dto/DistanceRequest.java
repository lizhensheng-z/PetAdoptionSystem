package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 距离计算请求DTO
 */
@Data
@Schema(description = "距离计算请求")
public class DistanceRequest {
    
    @Schema(description = "起点坐标")
    @NotNull(message = "起点坐标不能为空")
    private Coordinate from;
    
    @Schema(description = "终点坐标")
    @NotNull(message = "终点坐标不能为空")
    private Coordinate to;
    
    /**
     * 坐标点
     */
    @Data
    @Schema(description = "坐标点")
    public static class Coordinate {
        private Double lng;
        private Double lat;
    }
}