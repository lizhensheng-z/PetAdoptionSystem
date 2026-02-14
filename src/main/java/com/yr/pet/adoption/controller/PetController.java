package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 宠物管理控制器
 * @author yr
 * @since 2024-02-14
 */
@RestController
@RequestMapping("/api/pets")
@Tag(name = "宠物管理", description = "宠物列表、详情、推荐、搜索等相关接口")
@Validated
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取宠物列表（支持筛选、分页、排序）
     */
    @GetMapping
    @Operation(summary = "获取宠物列表", description = "获取宠物列表，支持关键词搜索、筛选、分页等功能")
    public R<PageResult<PetListResponse>> getPetList(@Valid PetListRequest request) {
        PageResult<PetListResponse> result = petService.getPetList(request);
        return R.ok(result);
    }

    /**
     * 获取宠物详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取宠物详情", description = "根据宠物ID获取详细信息")
    public R<PetDetailResponse> getPetDetail(
            @Parameter(description = "宠物ID", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long id) {
        PetDetailResponse detail = petService.getPetDetail(id);
        return R.ok(detail);
    }

    /**
     * 获取推荐宠物列表
     */
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐宠物列表", description = "基于用户偏好获取推荐宠物列表")
    public R<PageResult<PetListResponse>> getRecommendPets(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            
            @Parameter(description = "每页数量", example = "12")
            @RequestParam(defaultValue = "12") @Min(1) Integer pageSize,
            
            @Parameter(description = "用户经度", example = "116.404")
            @RequestParam(required = false) BigDecimal lng,
            
            @Parameter(description = "用户纬度", example = "39.915")
            @RequestParam(required = false) BigDecimal lat) {
        
        // 简化实现：目前不获取用户ID，实际应该根据token获取
        Long userId = null;
        PageResult<PetListResponse> result = petService.getRecommendPets(userId, page, pageSize, lng, lat);
        return R.ok(result);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggest")
    @Operation(summary = "获取搜索建议", description = "根据关键词获取搜索建议，包括品种、宠物名、标签等")
    public R<PetSuggestResponse> getSearchSuggestions(
            @Parameter(description = "搜索关键词", required = true, example = "橘猫")
            @RequestParam @NotNull String keyword) {
        
        PetSuggestResponse suggestions = petService.getSearchSuggestions(keyword);
        return R.ok(suggestions);
    }

    /**
     * 获取相似宠物列表
     */
    @GetMapping("/{id}/similar")
    @Operation(summary = "获取相似宠物列表", description = "获取与指定宠物相似的宠物列表")
    public R<List<SimilarPetResponse>> getSimilarPets(
            @Parameter(description = "宠物ID", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long id,
            
            @Parameter(description = "返回数量限制", example = "6")
            @RequestParam(defaultValue = "6") @Min(1) Integer limit) {
        
        List<SimilarPetResponse> similarPets = petService.getSimilarPets(id, limit);
        return R.ok(similarPets);
    }
}