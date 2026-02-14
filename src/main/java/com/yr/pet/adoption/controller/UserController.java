package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取用户详细信息", description = "获取用户详细信息，包含信用分、统计数据等")
    public R<UserDetailResponse> getUserProfile() {
        Long userId = UserContext.getUserId();
        UserDetailResponse response = userService.getUserDetail(userId);
        return R.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户统计数据", description = "获取用户的申请、收藏、打卡、领养等统计数据")
    public R<UserStatsResponse> getUserStats() {
        Long userId = UserContext.getUserId();
        UserStatsResponse stats = userService.getUserStats(userId);
        return R.ok(stats);
    }

    @GetMapping("/preference")
    @Operation(summary = "获取用户偏好设置", description = "获取用户的宠物偏好设置")
    public R<UserPreferenceResponse> getUserPreference() {
        Long userId = UserContext.getUserId();
        UserPreferenceResponse preference = userService.getUserPreference(userId);
        return R.ok(preference);
    }

    @PutMapping("/preference")
    @Operation(summary = "更新用户偏好设置", description = "更新用户的宠物偏好设置")
    public R<Void> updateUserPreference(@Valid @RequestBody UserPreferenceRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateUserPreference(userId, request);
        return R.ok();
    }
}
