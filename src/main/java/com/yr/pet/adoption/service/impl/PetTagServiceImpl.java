package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.yr.pet.adoption.mapper.PetTagMapper;
import com.yr.pet.adoption.service.PetTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 宠物-标签关联表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class PetTagServiceImpl extends ServiceImpl<PetTagMapper, PetTagEntity> implements PetTagService {

    @Autowired
    private PetTagMapper petTagMapper;

    @Override
    public List<Long> getTagIdsByPetId(Long petId) {
        return petTagMapper.selectTagIdsByPetId(petId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPetTags(Long petId, List<Long> tagIds) {
        // 删除宠物原有的所有标签关联
        LambdaQueryWrapper<PetTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetTagEntity::getPetId, petId);
        this.remove(wrapper);
        
        // 批量添加新的标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<PetTagEntity> petTags = tagIds.stream()
                .distinct() // 去重
                .map(tagId -> {
                    PetTagEntity petTag = new PetTagEntity();
                    petTag.setPetId(petId);
                    petTag.setTagId(tagId);
                    return petTag;
                })
                .collect(java.util.stream.Collectors.toList());
            
            this.saveBatch(petTags);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearPetTags(Long petId) {
        LambdaQueryWrapper<PetTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetTagEntity::getPetId, petId);
        this.remove(wrapper);
    }
}
