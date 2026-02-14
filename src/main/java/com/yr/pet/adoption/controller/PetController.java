package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.PetService;
import com.yr.pet.adoption.common.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 宠物管理控制器
 * @author yr
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "宠物管理", description = "宠物档案相关接口")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取宠物列表（游客/用户通用）
     */
    @GetMapping("/pets")
    @Operation(summary = "获取宠物列表", description = "获取公开的宠物列表，支持筛选和分页")
    public R<IPage<PetListResponse>> getPetList(@Valid PetQueryRequest request) {
        return R.ok(petService.getPetList(request));
    }

    /**
     * 获取宠物详情
     */
    @GetMapping("/pets/{petId}")
    @Operation(summary = "获取宠物详情", description = "获取指定宠物的详细信息")
    public R<PetDetailResponse> getPetDetail(
            @PathVariable Long petId,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat) {
        return R.ok(petService.getPetDetail(petId, lng, lat));
    }

    /**
     * 机构创建宠物档案
     */
    @PostMapping("/org/pets")
    @PreAuthorize("hasAuthority('pet:create')")
    @Operation(summary = "创建宠物档案", description = "机构创建新的宠物档案")
    public R<PetCreateResponse> createPet(@Valid @RequestBody PetCreateRequest request) {
        Long orgUserId = UserContext.getUserId();
        return R.ok(petService.createPet(orgUserId, request));
    }
}

