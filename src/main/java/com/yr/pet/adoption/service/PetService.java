package com.yr.pet.adoption.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.PetDetailResponse;
import com.yr.pet.adoption.model.dto.PetListRequest;
import com.yr.pet.adoption.model.dto.PetListResponse;
import com.yr.pet.adoption.model.dto.PetSuggestResponse;
import com.yr.pet.adoption.model.dto.SimilarPetResponse;
import com.yr.pet.adoption.model.entity.PetEntity;
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
}