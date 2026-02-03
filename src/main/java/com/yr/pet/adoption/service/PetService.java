package com.yr.pet.adoption.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.model.dto.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 宠物管理服务接口
 * @author yr
 * @since 2024-01-01
 */
public interface PetService {

    /**
     * 获取宠物列表（游客/用户通用）
     */
    IPage<PetListResponse> getPetList(PetQueryRequest request);

    /**
     * 获取宠物详情
     */
    PetDetailResponse getPetDetail(Long petId, Double lng, Double lat);

    /**
     * 机构创建宠物档案
     */
    PetCreateResponse createPet(Long orgUserId, PetCreateRequest request);

    /**
     * 机构修改宠物档案
     */
    void updatePet(Long orgUserId, Long petId, PetUpdateRequest request);

    /**
     * 机构上传宠物媒体
     */
    PetMediaUploadResponse uploadPetMedia(Long orgUserId, Long petId, MultipartFile file, String mediaType, Integer sort);

    /**
     * 机构删除宠物媒体
     */
    void deletePetMedia(Long orgUserId, Long petId, Long mediaId);

    /**
     * 机构提交宠物审核
     */
    PetAuditResponse submitPetAudit(Long orgUserId, Long petId);

    /**
     * 机构下架/删除宠物
     */
    void deletePet(Long orgUserId, Long petId, PetDeleteRequest request);

    /**
     * 机构查看自己的宠物列表
     */
    IPage<OrgPetListResponse> getOrgPetList(Long orgUserId, OrgPetQueryRequest request);
}