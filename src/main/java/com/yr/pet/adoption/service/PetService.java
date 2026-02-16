package com.yr.pet.adoption.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.model.entity.PetMediaEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 宠物服务接口
 * @author yr
 * @since 2024-02-14
 */
public interface PetService extends IService<PetEntity> {

    /**
     * 获取宠物列表（支持筛选、分页、排序）
     * @param request 查询参数
     * @return 分页结果
     */
    PageResult<PetListResponse> getPetList(PetListRequest request);

    /**
     * 获取宠物详情
     * @param id 宠物ID
     * @return 宠物详情
     */
    PetDetailResponse getPetDetail(Long id);

    /**
     * 获取推荐宠物列表（基于用户偏好）
     * @param userId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param lng 用户经度
     * @param lat 用户纬度
     * @return 推荐宠物列表
     */
    PageResult<PetListResponse> getRecommendPets(Long userId, Integer pageNo, Integer pageSize, 
                                                BigDecimal lng, BigDecimal lat);

    /**
     * 获取搜索建议
     * @param keyword 搜索关键词
     * @return 搜索建议
     */
    PetSuggestResponse getSearchSuggestions(String keyword);

    /**
     * 获取相似宠物列表
     * @param petId 当前宠物ID
     * @param limit 返回数量限制
     * @return 相似宠物列表
     */
    List<SimilarPetResponse> getSimilarPets(Long petId, Integer limit);

    // ==================== 机构宠物管理接口 ====================

    /**
     * 机构创建/发布宠物
     * @param orgUserId 机构用户ID
     * @param request 宠物创建请求
     * @return 创建的宠物信息
     */
    PetCreateResponse createPet(Long orgUserId, PetCreateRequest request);

    /**
     * 机构创建/发布宠物 V2 - 支持嵌套结构
     * @param orgUserId 机构用户ID
     * @param request 宠物创建请求V2
     * @return 创建的宠物信息
     */
    PetCreateResponse createPetV2(Long orgUserId, PetCreateRequestV2 request);

    /**
     * 机构更新宠物信息
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     * @param request 宠物更新请求
     */
    void updatePet(Long orgUserId, Long petId, PetUpdateRequest request);

    /**
     * 机构删除/下架宠物
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     */
    void deletePet(Long orgUserId, Long petId);

    /**
     * 机构获取自己的宠物列表
     * @param orgUserId 机构用户ID
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<OrgPetListResponse> getOrgPetList(Long orgUserId, OrgPetQueryRequest request);

    /**
     * 机构获取宠物详情
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     * @return 宠物详情
     */
    PetDetailResponse getOrgPetDetail(Long orgUserId, Long petId);

    /**
     * 保存宠物媒体关联
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     * @param request 媒体关联请求
     * @return 保存的媒体实体
     */
    PetMediaEntity savePetMedia(Long orgUserId, Long petId, PetMediaRequest request);

    /**
     * 删除宠物媒体
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     * @param mediaId 媒体ID
     */
    void deletePetMedia(Long orgUserId, Long petId, Long mediaId);

    /**
     * 提交宠物审核
     * @param orgUserId 机构用户ID
     * @param petId 宠物ID
     */
    void submitPetAudit(Long orgUserId, Long petId);
}