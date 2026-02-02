package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关操作
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class LoginController {
    @Resource
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return R.ok(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public R<UserEntity> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserEntity user = authService.register(registerRequest);
        return R.ok(user);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public R<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        LoginResponse response = authService.refreshToken(refreshTokenRequest);
        return R.ok(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口，将当前token加入黑名单")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public R<UserInfoResponse> getCurrentUserInfo() {
        UserInfoResponse userInfo = authService.getCurrentUserInfo();
        return R.ok(userInfo);
    }

    /**
     * 修改个人资料
     */
    @PutMapping("/profile")
    @Operation(summary = "修改个人资料", description = "修改当前用户的个人资料")
    public R<Void> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        authService.updateProfile(request);
        return R.ok();
    }
}
