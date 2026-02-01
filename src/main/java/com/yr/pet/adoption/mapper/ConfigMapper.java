package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.ConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统配置表（推荐权重/信用规则等） Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface ConfigMapper extends BaseMapper<ConfigEntity> {

}
