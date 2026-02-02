package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.TagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {
}