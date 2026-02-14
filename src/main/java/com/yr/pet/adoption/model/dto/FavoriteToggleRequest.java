package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏切换请求DTO
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "收藏切换请求")
public class FavoriteToggleRequest {
    
    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Long petId;
}