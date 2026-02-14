package com.yr.pet.adoption.service;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
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
     * 根据用户ID查找用户
     */
    UserEntity findById(Long id);
    
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
    
    /**
     * 更新用户最后登录时间
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 更新用户资料
     */
    void updateUserProfile(Long userId, UserProfileUpdateRequest request);
    
    /**
     * 将token加入黑名单
     */
    void addTokenToBlacklist(String username);

    /**
     * 获取用户详细信息（含信用分、统计数据）
     */
    UserDetailResponse getUserDetail(Long userId);

    /**
     * 获取用户统计数据
     */
    UserStatsResponse getUserStats(Long userId);

    /**
     * 获取用户偏好设置
     */
    UserPreferenceResponse getUserPreference(Long userId);

    /**
     * 更新用户偏好设置
     */
    void updateUserPreference(Long userId, UserPreferenceRequest request);
}
