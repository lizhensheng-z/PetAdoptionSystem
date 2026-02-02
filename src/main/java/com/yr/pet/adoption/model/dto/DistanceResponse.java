package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 距离计算响应DTO
 */
@Data
@Schema(description = "距离计算响应")
public class DistanceResponse {
    
    @Schema(description = "距离")
    private Double distance;
    
    @Schema(description = "单位")
    private String unit;
    
    @Schema(description = "预计时间")
    private Integer duration;
    
    @Schema(description = "时间单位")
    private String durationUnit;
}