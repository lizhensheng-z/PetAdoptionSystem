package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 申请列表包装响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "申请列表包装响应")
public class ApplicationListWrapperResponse {
    
    @Schema(description = "申请列表")
    private List<ApplicationListResponse> list;
}