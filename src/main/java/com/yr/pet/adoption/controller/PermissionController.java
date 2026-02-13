package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.PermissionService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.PageResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

/**
 * <p>
 * 权限管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "权限管理", description = "系统权限CRUD接口")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树形列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "获取权限树", description = "获取所有权限的树形结构")
    public R<List<PermissionResponse>> getPermissionTree() {
        return R.ok(permissionService.getPermissionTree());
    }

    /**
     * 获取权限列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "获取权限列表", description = "分页获取权限列表")
    public R<PageResult<PermissionResponse>> getPermissionList(
            @RequestParam(required = false) String permType,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(permissionService.getPermissionList(permType, pageNo, pageSize));
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限详情")
    public R<PermissionResponse> getPermissionById(@PathVariable Long id) {
        return R.ok(permissionService.getPermissionById(id));
    }

    /**
     * 创建权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "创建权限", description = "创建新的权限")
    public R createPermission(@Valid @RequestBody PermissionRequest request) {
        permissionService.createPermission(request);
        return R.ok("创建成功");
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "更新权限", description = "更新权限信息")
    public R updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        permissionService.updatePermission(id, request);
        return R.ok("更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:permission:manage')")
    @Operation(summary = "删除权限", description = "删除指定权限")
    public R deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return R.ok("删除成功");
    }
}
