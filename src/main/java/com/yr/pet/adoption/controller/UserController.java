package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.UserService;
import com.yr.pet.adoption.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理接口")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CreditService creditService;

    @GetMapping("/users/profile")
    @Operation(summary = "获取用户详细信息", description = "获取用户详细信息，包含信用分、统计数据等")
    public R<UserDetailResponse> getUserProfile() {
        Long userId = UserContext.getUserId();
        UserDetailResponse response = userService.getUserDetail(userId);
        return R.ok(response);
    }

    @GetMapping("/users/stats")
    @Operation(summary = "获取用户统计数据", description = "获取用户的申请、收藏、打卡、领养等统计数据")
    public R<UserStatsResponse> getUserStats() {
        Long userId = UserContext.getUserId();
        UserStatsResponse stats = userService.getUserStats(userId);
        return R.ok(stats);
    }

    @GetMapping("/users/preference")
    @Operation(summary = "获取用户偏好设置", description = "获取用户的宠物偏好设置")
    public R<UserPreferenceResponse> getUserPreference() {
        Long userId = UserContext.getUserId();
        UserPreferenceResponse preference = userService.getUserPreference(userId);
        return R.ok(preference);
    }

    @PutMapping("/users/preference")
    @Operation(summary = "更新用户偏好设置", description = "更新用户的宠物偏好设置")
    public R<Void> updateUserPreference(@Valid @RequestBody UserPreferenceRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateUserPreference(userId, request);
        return R.ok();
    }




    /**
     * 获取已领养宠物列表
     */
    @GetMapping("/user/pets/adopted")
//    @PreAuthorize("hasAuthority('user:adopted:pets')")
    @Operation(summary = "获取已领养宠物列表", description = "获取当前用户已领养成功的宠物列表")
    public R<List<UserAdoptedPetResponse>> getUserAdoptedPets() {
        Long userId = UserContext.getUserId();
        List<UserAdoptedPetResponse> response = userService.getUserAdoptedPets(userId);
        return R.ok(response);
    }

    /**
     * 获取信用摘要
     */
    @GetMapping("/user/credit/summary")
//    @PreAuthorize("hasAuthority('user:credit:summary')")
    @Operation(summary = "获取信用摘要", description = "获取当前用户的信用摘要信息，包括信用分、等级、排名等")
    public R<UserCreditSummaryResponse> getUserCreditSummary() {
        Long userId = UserContext.getUserId();
        UserCreditSummaryResponse response = creditService.getUserCreditSummary(userId);
        return R.ok(response);
    }

    /**
     * 获取信用积分变更流水
     */
    @GetMapping("/user/credit/logs")
//    @PreAuthorize("hasAuthority('user:credit:logs')")
    @Operation(summary = "获取信用积分变更流水", description = "获取用户信用积分的变更记录，用于展示积分明细")
    public R<PageResult<CreditLogResponse>> getUserCreditLogs(CreditLogsRequest request) {
        Long userId = UserContext.getUserId();
        PageResult<CreditLogResponse> response = creditService.getUserCreditLogs(userId, request);
        return R.ok(response);
    }
}
