package com.yr.pet.adoption.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 角色权限分配请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class RolePermissionRequest {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private List<Long> permissionIds;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}