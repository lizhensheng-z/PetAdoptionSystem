package com.yr.pet.adoption.service;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.PetAuditDetailResponse;
import com.yr.pet.adoption.model.dto.PetAuditRequest;
import com.yr.pet.adoption.model.dto.PendingPetResponse;
import com.yr.pet.adoption.model.entity.PetAuditEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 宠物发布审核服务接口
 * @author yr
 * @since 2026-02-01
 */
public interface PetAuditService extends IService<PetAuditEntity> {

    /**
     * 获取待审核宠物列表
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param species 物种筛选（可选）
     * @return 分页结果
     */
    PageResult<PendingPetResponse> getPendingPets(Integer pageNo, Integer pageSize, String species);

    /**
     * 获取待审核宠物详情
     * @param petId 宠物ID
     * @return 宠物审核详情
     */
    PetAuditDetailResponse getPetAuditDetail(Long petId);

    /**
     * 审核宠物
     * @param adminId 管理员ID
     * @param request 审核请求
     */
    void auditPet(Long adminId, PetAuditRequest request);

    /**
     * 获取待审核宠物数量
     * @return 待审核数量
     */
    long countPendingPets();
}