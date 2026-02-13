package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.PermissionMapper;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.PermissionEntity;
import com.yr.pet.adoption.service.PermissionService;
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
 * 权限表（API/菜单/按钮） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {

    @Override
    public List<PermissionResponse> getPermissionTree() {
        List<PermissionEntity> allPermissions = this.list(new LambdaQueryWrapper<PermissionEntity>()
                .orderByAsc(PermissionEntity::getSort));

        List<PermissionResponse> rootPermissions = new ArrayList<>();
        Map<Long, List<PermissionEntity>> parentMap = allPermissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));

        for (PermissionEntity entity : allPermissions) {
            if (entity.getParentId() == null || entity.getParentId() == 0) {
                PermissionResponse response = convertToResponse(entity);
                response.setChildren(buildChildren(entity, parentMap));
                rootPermissions.add(response);
            }
        }

        return rootPermissions;
    }

    private List<PermissionResponse> buildChildren(PermissionEntity parent, Map<Long, List<PermissionEntity>> parentMap) {
        List<PermissionResponse> children = new ArrayList<>();
        List<PermissionEntity> childList = parentMap.get(parent.getId());

        if (childList != null && !childList.isEmpty()) {
            for (PermissionEntity child : childList) {
                PermissionResponse response = convertToResponse(child);
                response.setChildren(buildChildren(child, parentMap));
                children.add(response);
            }
        }
        return children;
    }

    @Override
    public PageResult<PermissionResponse> getPermissionList(String permType, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<PermissionEntity> query = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(permType)) {
            query.eq(PermissionEntity::getPermType, permType);
        }
        
        query.orderByAsc(PermissionEntity::getSort);
        
        Page<PermissionEntity> page = new Page<>(pageNo, pageSize);
        Page<PermissionEntity> result = this.page(page, query);
        
        List<PermissionResponse> list = new ArrayList<>();
        for (PermissionEntity entity : result.getRecords()) {
            list.add(convertToResponse(entity));
        }
        
        return new PageResult<>(list, pageNo, pageSize, result.getTotal(),
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        PermissionEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "权限不存在");
        }
        return convertToResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPermission(PermissionRequest request) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<PermissionEntity> query = new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getPermCode, request.getPermCode());
        if (this.count(query) > 0) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "权限编码已存在");
        }

        PermissionEntity entity = new PermissionEntity();
        BeanUtils.copyProperties(request, entity);
        
        if (entity.getEnabled() == null) {
            entity.setEnabled((byte) 1);
        }
        
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long id, PermissionRequest request) {
        PermissionEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "权限不存在");
        }

        // 如果修改了权限编码，检查是否已存在
        if (!entity.getPermCode().equals(request.getPermCode())) {
            LambdaQueryWrapper<PermissionEntity> query = new LambdaQueryWrapper<PermissionEntity>()
                    .eq(PermissionEntity::getPermCode, request.getPermCode())
                    .ne(PermissionEntity::getId, id);
            if (this.count(query) > 0) {
                throw new BizException(ErrorCode.RESOURCE_EXIST, "权限编码已存在");
            }
        }

        BeanUtils.copyProperties(request, entity, "id", "createTime");
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        PermissionEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "权限不存在");
        }

        // 检查是否有子权限
        LambdaQueryWrapper<PermissionEntity> query = new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getParentId, id);
        if (this.count(query) > 0) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "请先删除子权限");
        }

        this.removeById(id);
    }

    private PermissionResponse convertToResponse(PermissionEntity entity) {
        PermissionResponse response = new PermissionResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
