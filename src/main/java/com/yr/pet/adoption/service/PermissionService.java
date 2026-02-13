package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.PermissionEntity;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限表（API/菜单/按钮） 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface PermissionService extends IService<PermissionEntity> {

    /**
     * 获取权限列表（树形）
     */
    List<PermissionResponse> getPermissionTree();

    /**
     * 获取所有权限列表
     */
    PageResult<PermissionResponse> getPermissionList(String permType, Integer pageNo, Integer pageSize);

    /**
     * 获取权限详情
     */
    PermissionResponse getPermissionById(Long id);

    /**
     * 创建权限
     */
    void createPermission(PermissionRequest request);

    /**
     * 更新权限
     */
    void updatePermission(Long id, PermissionRequest request);

    /**
     * 删除权限
     */
    void deletePermission(Long id);
}
