package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.PetMediaRequest;
import com.yr.pet.adoption.model.entity.PetMediaEntity;
import com.yr.pet.adoption.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 宠物媒体管理控制器
 * @author yr
 * @since 2026-02-15
 */
@RestController
@RequestMapping("/api/org")
@Tag(name = "宠物媒体管理", description = "宠物图片、视频等媒体文件管理接口")
@Validated
public class PetMediaController {

    @Autowired
    private PetService petService;

    /**
     * 保存宠物媒体关联
     */
    @PostMapping("/pets/{id}/media")
    @PreAuthorize("hasAuthority('pet:create')")
    @Operation(summary = "保存宠物媒体关联", description = "将已上传的文件URL与宠物关联")
    public R<PetMediaEntity> savePetMedia(
            @Parameter(description = "宠物ID", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody PetMediaRequest request) {
        
        Long userId = UserContext.getUserId();
        PetMediaEntity media = petService.savePetMedia(userId, id, request);
        return R.ok(media);
    }

    /**
     * 删除宠物媒体
     */
    @DeleteMapping("/pets/{petId}/media/{mediaId}")
    @PreAuthorize("hasAuthority('pet:update')")
    @Operation(summary = "删除宠物媒体", description = "删除指定的宠物媒体文件")
    public R<Void> deletePetMedia(
            @Parameter(description = "宠物ID", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long petId,
            @Parameter(description = "媒体ID", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long mediaId) {
        
        Long userId = UserContext.getUserId();
        petService.deletePetMedia(userId, petId, mediaId);
        return R.ok();
    }
}
