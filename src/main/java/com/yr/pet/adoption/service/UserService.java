package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.RegisterRequest;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表（领养人/机构/管理员） 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 根据用户名查找用户
     */
    UserEntity findByUsername(String username);
    
    /**
     * 用户注册
     */
    UserEntity register(RegisterRequest registerRequest);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
