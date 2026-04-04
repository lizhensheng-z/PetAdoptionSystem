package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.*;
import com.yr.pet.adoption.model.dto.*;
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
    @Autowired
    private UserContextHolder userContextHolder;
    @Autowired
    private UserContent userContent;

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
        Long userId = UserContext.getUserId();
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

// ==================== 机构宠物管理接口 ====================

    /**
     *   弃用
     * 机构创建/发布宠物
     */
//    @Deprecated
//    @PostMapping("/org/createPet")
//    @Operation(summary = "机构创建宠物", description = "机构创建新的宠物档案")
//    public R<PetCreateResponse> createPet(@Valid @RequestBody PetCreateRequest request) {
//        Long userId = com.yr.pet.adoption.common.UserContext.getUserId();
//        PetCreateResponse response = petService.createPet(userId, request);
//        return R.ok(response);
//    }

    /**
     * 机构创建/发布宠物 V2 - 支持嵌套结构
     */
    @PostMapping("/org/createPet")
    @Operation(summary = "机构创建宠物", description = "机构创建新的宠物档案，支持嵌套JSON格式")
    public R<PetCreateResponse> createPetV2(@Valid @RequestBody PetCreateRequestV2 request) {
        Long userId = UserContext.getUserId();
        PetCreateResponse response = petService.createPetV2(userId, request);
        return R.ok(response);
    }

    /**
     * 机构更新宠物信息
     */
    @PutMapping("/org/{id}")
    @Operation(summary = "机构更新宠物信息", description = "机构更新指定宠物的详细信息")
    public R<Void> updatePet(
            @Parameter(description = "宠物ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody PetUpdateRequest request) {

        Long userId = com.yr.pet.adoption.common.UserContext.getUserId();
        petService.updatePet(userId, id, request);
        return R.ok();
    }

    /**
     * 机构删除/下架宠物
     */
    @DeleteMapping("/org/{id}")
    @Operation(summary = "机构删除宠物", description = "机构删除指定宠物")
    public R<Void> deletePet(
            @Parameter(description = "宠物ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {

        Long userId = com.yr.pet.adoption.common.UserContext.getUserId();
        petService.deletePet(userId, id);
        return R.ok();
    }

    /**
     * 发布宠物
     */
    @PostMapping("/org/pets/{id}/publish")
    @Operation(summary = "发布宠物", description = "将宠物档案发布，发布后用户可见")
    public R<Void> publishPet(
            @Parameter(description = "宠物ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {

        Long userId = com.yr.pet.adoption.common.UserContext.getUserId();
        petService.publishPet(userId, id);
        return R.ok();
    }

    /**
     * 机构获取自己的宠物列表
     */
    @GetMapping("/org/my-pets")
    @Operation(summary = "机构获取宠物列表", description = "机构获取自己发布的宠物列表")
    public R<PageResult<OrgPetListResponse>> getOrgPetList(@Valid OrgPetQueryRequest request) {
        Long userId = UserContext.getUserId();
        PageResult<OrgPetListResponse> result = petService.getOrgPetList(userId, request);
        return R.ok(result);
    }

    /**
     * 机构获取宠物详情
     */
    @GetMapping("/org/detail/{id}")
    @Operation(summary = "机构获取宠物详情", description = "机构获取指定宠物的详细信息")
    public R<PetDetailResponse> getOrgPetDetail(
            @Parameter(description = "宠物ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {

        Long userId = com.yr.pet.adoption.common.UserContext.getUserId();
        PetDetailResponse detail = petService.getOrgPetDetail(userId, id);
        return R.ok(detail);
    }
}