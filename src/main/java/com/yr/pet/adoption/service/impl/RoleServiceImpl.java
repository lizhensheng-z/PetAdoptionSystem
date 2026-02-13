package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.service.RoleService;
import com.yr.pet.adoption.common.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public RoleServiceImpl(RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PageResult<RoleResponse> getRoleList(Integer pageNo, Integer pageSize) {
        Page<RoleEntity> page = new Page<>(pageNo, pageSize);
        Page<RoleEntity> result = this.page(page, new LambdaQueryWrapper<RoleEntity>()
                .orderByDesc(RoleEntity::getCreateTime));

        List<RoleResponse> list = new ArrayList<>();
        for (RoleEntity entity : result.getRecords()) {
            RoleResponse response = convertToResponse(entity);
            // 获取角色的权限数量
            long permCount = rolePermissionMapper.selectCount(
                    new LambdaQueryWrapper<RolePermissionEntity>()
                            .eq(RolePermissionEntity::getRoleId, entity.getId()));
            list.add(response);
        }

        return new PageResult<>(list, pageNo, pageSize, result.getTotal(),
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        RoleEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        RoleResponse response = convertToResponse(entity);
        response.setPermissions(getRolePermissions(id));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleRequest request) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<RoleEntity> query = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, request.getRoleCode());
        if (this.count(query) > 0) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "角色编码已存在");
        }

        RoleEntity entity = new RoleEntity();
        BeanUtils.copyProperties(request, entity);

        if (entity.getEnabled() == null) {
            entity.setEnabled((byte) 1);
        }

        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleRequest request) {
        RoleEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        // 如果修改了角色编码，检查是否已存在
        if (!entity.getRoleCode().equals(request.getRoleCode())) {
            LambdaQueryWrapper<RoleEntity> query = new LambdaQueryWrapper<RoleEntity>()
                    .eq(RoleEntity::getRoleCode, request.getRoleCode())
                    .ne(RoleEntity::getId, id);
            if (this.count(query) > 0) {
                throw new BizException(ErrorCode.RESOURCE_EXIST, "角色编码已存在");
            }
        }

        BeanUtils.copyProperties(request, entity, "id", "createTime");
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        RoleEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        // 检查是否有关联用户
        // 这里简化处理，直接删除
        this.removeById(id);

        // 删除角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, id));
    }

    @Override
    public List<PermissionResponse> getRolePermissions(Long roleId) {
        // 获取角色所有权限ID
        List<RolePermissionEntity> rolePerms = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getRoleId, roleId));

        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permIds = rolePerms.stream()
                .map(RolePermissionEntity::getPermissionId)
                .collect(Collectors.toList());

        // 批量查询权限信息
        List<PermissionEntity> permissions = permissionMapper.selectBatchIds(permIds);

        List<PermissionResponse> result = new ArrayList<>();
        for (PermissionEntity entity : permissions) {
            PermissionResponse response = new PermissionResponse();
            BeanUtils.copyProperties(entity, response);
            result.add(response);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RolePermissionRequest request) {
        RoleEntity role = this.getById(request.getRoleId());
        if (role == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        // 删除原有权限
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, request.getRoleId()));

        // 添加新权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (Long permId : request.getPermissionIds()) {
                PermissionEntity perm = permissionMapper.selectById(permId);
                if (perm == null) {
                    throw new BizException(ErrorCode.NOT_FOUND, "权限ID不存在: " + permId);
                }

                RolePermissionEntity rolePerm = new RolePermissionEntity();
                rolePerm.setRoleId(request.getRoleId());
                rolePerm.setPermissionId(permId);
                rolePermissionMapper.insert(rolePerm);
            }
        }
    }

    private RoleResponse convertToResponse(RoleEntity entity) {
        RoleResponse response = new RoleResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
