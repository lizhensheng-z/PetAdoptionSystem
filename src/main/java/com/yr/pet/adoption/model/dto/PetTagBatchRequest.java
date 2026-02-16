package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 宠物标签批量操作请求DTO
 * @author yr
 * @since 2026-02-16
 */
@Data
@Schema(description = "宠物标签批量操作请求")
public class PetTagBatchRequest {
    
    @NotNull(message = "宠物ID不能为空")
    @Schema(description = "宠物ID", example = "1")
    private Long petId;
    
    @NotEmpty(message = "标签ID列表不能为空")
    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;
}