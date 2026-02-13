package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.RoleEntity;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface RoleService extends IService<RoleEntity> {

    /**
     * 获取角色列表
     */
    PageResult<RoleResponse> getRoleList(Integer pageNo, Integer pageSize);

    /**
     * 获取角色详情
     */
    RoleResponse getRoleById(Long id);

    /**
     * 创建角色
     */
    void createRole(RoleRequest request);

    /**
     * 更新角色
     */
    void updateRole(Long id, RoleRequest request);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 获取角色权限
     */
    List<PermissionResponse> getRolePermissions(Long roleId);

    /**
     * 分配角色权限
     */
    void assignPermissions(RolePermissionRequest request);
}
