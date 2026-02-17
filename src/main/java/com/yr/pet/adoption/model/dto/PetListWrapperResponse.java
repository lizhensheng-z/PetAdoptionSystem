package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 宠物列表包装响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "宠物列表包装响应")
public class PetListWrapperResponse {
    
    @Schema(description = "宠物列表")
    private List<PetListResponse> list;
}