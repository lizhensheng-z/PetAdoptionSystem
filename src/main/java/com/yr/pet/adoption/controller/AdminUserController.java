package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 管理员用户管理控制器
 * 提供用户管理相关接口，包括用户列表、详情、状态管理等功能
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "管理员用户管理", description = "系统管理员用户管理接口")
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持搜索和筛选")
    public R<PageResult<UserAdminResponse>> getUserList(@Valid UserListRequest request) {
        PageResult<UserAdminResponse> result = userService.getUserList(request);
        return R.ok(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "获取用户详情", description = "获取单个用户的详细信息")
    public R<UserDetailAdminResponse> getUserDetail(@PathVariable Long id) {
        UserDetailAdminResponse response = userService.getUserDetails(id);
        return R.ok(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息（手机号、邮箱、角色、头像、状态）")
    public R updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return R.ok("更新成功");
    }

    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch-status")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户账号")
    public R batchUpdateUserStatus(@Valid @RequestBody BatchUpdateStatusRequest request) {
        userService.batchUpdateUserStatus(request.getUserIds(), request.getStatus());
        return R.ok("操作成功");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('user:delete')")
    @Operation(summary = "批量删除用户", description = "批量删除用户（软删除）")
    public R batchDeleteUsers(@Valid @RequestBody BatchDeleteUsersRequest request) {
        userService.batchDeleteUsers(request.getUserIds());
        return R.ok("删除成功");
    }

    /**
     * 导出用户数据
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('user:export')")
    @Operation(summary = "导出用户数据", description = "导出用户数据到Excel文件")
    public void exportUsers(@Valid UserListRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头
        String fileName = "用户数据_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setCharacterEncoding("UTF-8");

        // 调用服务导出数据
        userService.exportUsers(request, response);
    }
}