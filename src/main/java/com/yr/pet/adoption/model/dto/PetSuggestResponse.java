package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 宠物搜索建议响应数据
 * @author yr
 * @since 2026-02-14
 */
@Data
@Schema(description = "宠物搜索建议响应数据")
public class PetSuggestResponse {

    @Schema(description = "品种建议列表")
    private List<String> breeds;

    @Schema(description = "宠物名称建议列表")
    private List<String> keywords;

    @Schema(description = "标签建议列表")
    private List<String> tags;
}