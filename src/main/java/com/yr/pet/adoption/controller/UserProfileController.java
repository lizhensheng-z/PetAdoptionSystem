package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户个人信息控制器
 * 演示如何使用用户上下文获取当前登录用户信息
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户个人信息相关接口")
public class UserProfileController {

    private final UserService userService;
    private final UserContent userContent;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public R<UserContext> getCurrentUser() {
        UserContext currentUser = userContent.getUserContext();
        if (currentUser == null) {
            return R.fail("用户未登录");
        }
        return R.ok(currentUser);
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