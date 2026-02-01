package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统用户表（领养人/机构/管理员） Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
