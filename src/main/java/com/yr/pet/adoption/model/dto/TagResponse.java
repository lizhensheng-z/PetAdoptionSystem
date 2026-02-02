package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 标签响应DTO
 */
@Data
@Schema(description = "标签响应")
public class TagResponse {
    
    @Schema(description = "标签列表")
    private List<TagGroup> tags;
    
    /**
     * 标签分组
     */
    @Data
    @Schema(description = "标签分组")
    public static class TagGroup {
        private String type;
        private List<TagItem> items;
    }
    
    /**
     * 标签项
     */
    @Data
    @Schema(description = "标签项")
    public static class TagItem {
        private Long id;
        private String name;
    }
}