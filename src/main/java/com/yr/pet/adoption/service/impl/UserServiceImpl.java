package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.model.dto.RegisterRequest;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public UserEntity findByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
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
}
