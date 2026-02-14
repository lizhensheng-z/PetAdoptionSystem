package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏状态检查响应DTO
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "收藏状态检查响应")
public class FavoriteCheckResponse {
    
    @Schema(description = "是否已收藏", example = "true")
    private Boolean favorited;
    
    @Schema(description = "收藏ID（如果已收藏）", example = "123")
    private Long favoriteId;
}