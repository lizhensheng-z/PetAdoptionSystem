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
import org.springframework.web.multipart.MultipartFile;


/**
 * 宠物管理控制器
 * @author yr
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取宠物列表（游客/用户通用）
     */
    @GetMapping("/pets")
    public R<IPage<PetListResponse>> getPetList(@Valid PetQueryRequest request) {
        return R.ok(petService.getPetList(request));
    }

    /**
     * 获取宠物详情
     */
    @GetMapping("/pets/{petId}")
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
    public R<PetCreateResponse> createPet(@Valid @RequestBody PetCreateRequest request) {
        Long orgUserId = UserContext.getUserId();
        return R.ok(petService.createPet(orgUserId, request));
    }

    /**
     * 机构修改宠物档案
     */
    @PutMapping("/org/pets/{petId}")
    @PreAuthorize("hasAuthority('pet:update')")
    public R<Void> updatePet(
            @PathVariable Long petId,
            @Valid @RequestBody PetUpdateRequest request) {
        Long orgUserId = UserContext.getUserId();
        petService.updatePet(orgUserId, petId, request);
        return R.ok();
    }

    /**
     * 机构上传宠物媒体
     */
    @PostMapping("/org/pets/{petId}/media")
    @PreAuthorize("hasAuthority('pet:update')")
    public R<PetMediaUploadResponse> uploadPetMedia(
            @PathVariable Long petId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String mediaType,
            @RequestParam(required = false) Integer sort) {
        Long orgUserId = UserContext.getUserId();
        return R.ok(petService.uploadPetMedia(orgUserId, petId, file, mediaType, sort));
    }

    /**
     * 机构删除宠物媒体
     */
    @DeleteMapping("/org/pets/{petId}/media/{mediaId}")
    @PreAuthorize("hasAuthority('pet:update')")
    public R<Void> deletePetMedia(
            @PathVariable Long petId,
            @PathVariable Long mediaId) {
        Long orgUserId = UserContext.getUserId();
        petService.deletePetMedia(orgUserId, petId, mediaId);
        return R.ok();
    }

    /**
     * 机构提交宠物审核
     */
    @PostMapping("/org/pets/{petId}/submit-audit")
    @PreAuthorize("hasAuthority('pet:submit_audit')")
    public R<PetAuditResponse> submitPetAudit(@PathVariable Long petId) {
        Long orgUserId = UserContext.getUserId();
        return R.ok(petService.submitPetAudit(orgUserId, petId));
    }

    /**
     * 机构下架/删除宠物
     */
    @DeleteMapping("/org/pets/{petId}")
    @PreAuthorize("hasAuthority('pet:remove')")
    public R<Void> deletePet(
            @PathVariable Long petId,
            @RequestBody(required = false) PetDeleteRequest request) {
        Long orgUserId = UserContext.getUserId();
        if (request == null) {
            request = new PetDeleteRequest();
        }
        petService.deletePet(orgUserId, petId, request);
        return R.ok(null);
    }

    /**
     * 机构查看自己的宠物列表
     */
    @GetMapping("/org/pets")
    @PreAuthorize("hasAuthority('pet:read')")
    public R<IPage<OrgPetListResponse>> getOrgPetList(@Valid OrgPetQueryRequest request) {
        Long orgUserId = UserContext.getUserId();
        return R.ok(petService.getOrgPetList(orgUserId, request));
    }
}