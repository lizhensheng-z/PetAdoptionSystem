package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.PetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 宠物档案表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface PetMapper extends BaseMapper<PetEntity> {

}
