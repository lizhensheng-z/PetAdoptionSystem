package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.RoleService;
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
 * 角色管理接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "角色管理", description = "系统角色CRUD接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    public R<PageResult<RoleResponse>> getRoleList(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(roleService.getRoleList(pageNo, pageSize));
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情，包含权限列表")
    public R<RoleResponse> getRoleById(@PathVariable Long id) {
        return R.ok(roleService.getRoleById(id));
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "创建角色", description = "创建新的角色")
    public R createRole(@Valid @RequestBody RoleRequest request) {
        roleService.createRole(request);
        return R.ok("创建成功");
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public R updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        roleService.updateRole(id, request);
        return R.ok("更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "删除角色", description = "删除指定角色")
    public R deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok("删除成功");
    }

    /**
     * 获取角色权限
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    public R<List<PermissionResponse>> getRolePermissions(@PathVariable Long id) {
        return R.ok(roleService.getRolePermissions(id));
    }

    /**
     * 分配角色权限
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('admin:role:manage')")
    @Operation(summary = "分配角色权限", description = "为角色分配权限")
    public R assignPermissions(@PathVariable Long id, @Valid @RequestBody RolePermissionRequest request) {
        request.setRoleId(id);
        roleService.assignPermissions(request);
        return R.ok("权限分配成功");
    }
}
