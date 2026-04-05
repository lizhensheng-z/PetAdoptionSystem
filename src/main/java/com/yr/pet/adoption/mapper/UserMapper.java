package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * @author yr
 * @since 2026-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}