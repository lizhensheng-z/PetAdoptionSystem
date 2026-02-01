package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.PetTagEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 宠物-标签关联表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface PetTagMapper extends BaseMapper<PetTagEntity> {

}
