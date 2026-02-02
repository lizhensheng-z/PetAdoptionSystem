package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.model.dto.RegisterRequest;
import com.yr.pet.adoption.model.dto.UserProfileUpdateRequest;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 系统用户表（领养人/机构/管理员） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public UserEntity findByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDeleted, 0)
                .one();
    }

    @Override
    public UserEntity findById(Long id) {
        return lambdaQuery()
                .eq(UserEntity::getId, id)
                .eq(UserEntity::getDeleted, 0)
                .one();
    }

    @Override
    public UserEntity register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (registerRequest.getPhone() != null && existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setRole(registerRequest.getRole());
        user.setStatus("NORMAL");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        save(user);
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return lambdaQuery()
                .eq(UserEntity::getPhone, phone)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return lambdaQuery()
                .eq(UserEntity::getEmail, email)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(UserEntity::getLastLoginTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(request.getAvatar() != null, UserEntity::getAvatar, request.getAvatar())
                .set(request.getPhone() != null, UserEntity::getPhone, request.getPhone())
                .set(request.getEmail() != null, UserEntity::getEmail, request.getEmail())
                .set(request.getPreference() != null, UserEntity::getPreferenceJson, convertPreferenceToJson(request.getPreference()))
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void addTokenToBlacklist(String username) {
        // 将用户相关的token加入黑名单，实现登出功能
        String key = "token:blacklist:" + username;
        redisTemplate.opsForValue().set(key, "true", 1, TimeUnit.HOURS);
    }

    private String convertPreferenceToJson(UserProfileUpdateRequest.UserPreference preference) {
        if (preference == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(preference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("用户偏好设置转换失败", e);
        }
    }
}
