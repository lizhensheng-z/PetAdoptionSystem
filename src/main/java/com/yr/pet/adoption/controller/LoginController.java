package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.LoginRequest;
import com.yr.pet.adoption.model.dto.LoginResponse;
import com.yr.pet.adoption.model.dto.RegisterRequest;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用用户名刷新JWT令牌")
    public R<LoginResponse> refresh(@RequestBody String username) {
        LoginResponse response = authService.refreshToken(username);
        return R.ok(response);
    }
}
