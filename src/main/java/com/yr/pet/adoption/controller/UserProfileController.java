package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.model.dto.ChangePasswordRequest;
import com.yr.pet.adoption.model.dto.UserInfoResponse;
import com.yr.pet.adoption.model.dto.UserProfileUpdateRequest;
import com.yr.pet.adoption.model.entity.UserEntity;

import com.yr.pet.adoption.service.AuthService;
import com.yr.pet.adoption.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人信息控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户个人信息相关接口")
public class UserProfileController {

    private final UserService userService;
    private final UserContent userContent;
    private final AuthService authService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public R<UserInfoResponse> getCurrentUser() {
        UserInfoResponse userInfo = authService.getCurrentUserInfo();
        return R.ok(userInfo);
    }

    /**
     * 更新当前用户个人资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人资料（头像、手机号、邮箱等）")
    public R<Void> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        authService.updateProfile(request);
        return R.ok();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public R<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = userContent.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }
        userService.changePassword(userId, request);
        return R.ok();
    }

    /**
     * 获取当前用户ID
     */
    @GetMapping("/id")
    @Operation(summary = "获取当前用户ID", description = "获取当前登录用户的ID")
    public R<Long> getCurrentUserId() {
        Long userId = userContent.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }
        return R.ok(userId);
    }

    /**
     * 获取当前用户名
     */
    @GetMapping("/username")
    @Operation(summary = "获取当前用户名", description = "获取当前登录用户的用户名")
    public R<String> getCurrentUsername() {
        String username = userContent.getUsername();
        if (username == null) {
            return R.fail("用户未登录");
        }
        return R.ok(username);
    }

    /**
     * 检查当前用户是否有指定角色
     */
    @GetMapping("/has-role")
    @Operation(summary = "检查用户角色", description = "检查当前用户是否有指定角色")
    public R<Boolean> hasRole(String role) {
        boolean hasRole = userContent.hasRole(role);
        return R.ok(hasRole);
    }

    /**
     * 检查当前用户是否有指定权限
     */
    @GetMapping("/has-permission")
    @Operation(summary = "检查用户权限", description = "检查当前用户是否有指定权限")
    public R<Boolean> hasPermission(String permission) {
        boolean hasPermission = userContent.hasPermission(permission);
        return R.ok(hasPermission);
    }
}