package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.PetAuditEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 宠物发布审核记录表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface PetAuditMapper extends BaseMapper<PetAuditEntity> {

}
