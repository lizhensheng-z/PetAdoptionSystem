package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签字典表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagEntity::getName, name);
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean existsByNameExcludeId(String name, Long excludeId) {
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagEntity::getName, name)
               .ne(TagEntity::getId, excludeId);
        return this.count(wrapper) > 0;
    }
}
