package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.PetAuditDetailResponse;
import com.yr.pet.adoption.model.dto.PetAuditRequest;
import com.yr.pet.adoption.model.dto.PendingPetResponse;
import com.yr.pet.adoption.service.PetAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物发布审核控制器
 * @author yr
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/admin/pets")
@RequiredArgsConstructor
@Tag(name = "宠物审核管理", description = "管理员审核宠物发布相关接口")
public class PetAuditController {

    private final PetAuditService petAuditService;

    /**
     * 获取待审核宠物列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('admin:pet:audit')")
    @Operation(summary = "获取待审核宠物列表", description = "获取等待审核的宠物发布申请列表")
    public R<PageResult<PendingPetResponse>> getPendingPets(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNo,

            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @Parameter(description = "物种筛选：CAT/DOG/OTHER", example = "CAT")
            @RequestParam(required = false) String species) {

        PageResult<PendingPetResponse> result = petAuditService.getPendingPets(pageNo, pageSize, species);
        return R.ok(result);
    }

    /**
     * 获取待审核宠物详情
     */
    @GetMapping("/pending/{petId}")
    @PreAuthorize("hasAuthority('admin:pet:audit')")
    @Operation(summary = "获取待审核宠物详情", description = "获取指定宠物的审核详情信息")
    public R<PetAuditDetailResponse> getPetAuditDetail(
            @Parameter(description = "宠物ID", required = true)
            @PathVariable Long petId) {

        PetAuditDetailResponse detail = petAuditService.getPetAuditDetail(petId);
        return R.ok(detail);
    }

    /**
     * 审核宠物
     */
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('admin:pet:audit')")
    @Operation(summary = "审核宠物", description = "管理员审核宠物发布申请，支持通过或拒绝")
    public R<Void> auditPet(@Valid @RequestBody PetAuditRequest request) {
        Long adminId = UserContext.getUserId();
        petAuditService.auditPet(adminId, request);
        return R.ok();
    }

    /**
     * 获取待审核宠物数量
     */
    @GetMapping("/pending/count")
    @PreAuthorize("hasAuthority('admin:pet:audit')")
    @Operation(summary = "获取待审核宠物数量", description = "获取当前待审核宠物的总数")
    public R<Long> countPendingPets() {
        long count = petAuditService.countPendingPets();
        return R.ok(count);
    }
}