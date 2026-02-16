package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 宠物标签响应DTO
 * @author yr
 * @since 2026-02-16
 */
@Data
@Schema(description = "宠物标签响应")
public class PetTagResponse {
    
    @Schema(description = "宠物ID")
    private Long petId;
    
    @Schema(description = "标签列表")
    private List<TagInfo> tags;
    
    /**
     * 标签信息
     */
    @Data
    @Schema(description = "标签信息")
    public static class TagInfo {
        @Schema(description = "标签ID")
        private Long id;
        
        @Schema(description = "标签名称")
        private String name;
        
        @Schema(description = "标签类型")
        private String tagType;
    }
}