package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏切换响应DTO
 * @author yr
 * @since 2026-02-14
 */
@Data
@Schema(description = "收藏切换响应")
public class FavoriteToggleResponse {
    
    @Schema(description = "当前收藏状态：true=已收藏，false=已取消收藏", example = "true")
    private Boolean favorited;
    
    @Schema(description = "收藏ID（如果已收藏）", example = "123")
    private Long favoriteId;
    
    @Schema(description = "操作消息", example = "收藏成功")
    private String message;
}