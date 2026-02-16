package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 宠物-标签关联表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface PetTagService extends IService<PetTagEntity> {

    /**
     * 获取宠物的标签ID列表
     * @param petId 宠物ID
     * @return 标签ID列表
     */
    List<Long> getTagIdsByPetId(Long petId);

    /**
     * 批量设置宠物的标签
     * @param petId 宠物ID
     * @param tagIds 标签ID列表
     */
    void setPetTags(Long petId, List<Long> tagIds);

    /**
     * 删除宠物的所有标签
     * @param petId 宠物ID
     */
    void clearPetTags(Long petId);
}
