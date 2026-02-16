package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.ConfigMapper;
import com.yr.pet.adoption.model.dto.ConfigRequest;
import com.yr.pet.adoption.model.dto.ConfigResponse;
import com.yr.pet.adoption.model.entity.ConfigEntity;
import com.yr.pet.adoption.service.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * <p>
 * 系统配置表（推荐权重/信用规则等） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Override
    public PageResult<ConfigResponse> listConfigs(Integer pageNo, Integer pageSize, String configKey, String remark) {
        // 参数校验
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        // 构建查询条件
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<ConfigEntity>();
        
        if (StringUtils.hasText(configKey)) {
            queryWrapper.like(ConfigEntity::getConfigKey, configKey);
        }
        
        if (StringUtils.hasText(remark)) {
            queryWrapper.like(ConfigEntity::getRemark, remark);
        }
        
        queryWrapper.orderByDesc(ConfigEntity::getUpdateTime);

        // 分页查询
        Page<ConfigEntity> page = new Page<>(pageNo, pageSize);
        IPage<ConfigEntity> result = this.page(page, queryWrapper);

        // 转换结果
        List<ConfigResponse> list = result.getRecords().stream().map(this::convertToResponse).toList();
        return new PageResult<>(list, pageNo, pageSize, result.getTotal(), (int) result.getPages());
    }

    @Override
    public ConfigResponse createConfig(ConfigRequest request) {
        // 检查配置键是否已存在
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<ConfigEntity>()
                .eq(ConfigEntity::getConfigKey, request.getConfigKey());
        
        if (this.count(queryWrapper) > 0) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "配置键已存在: " + request.getConfigKey());
        }

        // 创建配置实体
        ConfigEntity entity = new ConfigEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setUpdateTime(null); // 让数据库自动设置

        this.save(entity);
        return convertToResponse(entity);
    }

    @Override
    public ConfigResponse updateConfig(Long id, ConfigRequest request) {
        // 检查配置是否存在
        ConfigEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "配置不存在");
        }

        // 检查配置键是否被其他配置使用
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<ConfigEntity>()
                .eq(ConfigEntity::getConfigKey, request.getConfigKey())
                .ne(ConfigEntity::getId, id);
        
        if (this.count(queryWrapper) > 0) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "配置键已存在: " + request.getConfigKey());
        }

        // 更新配置
        BeanUtils.copyProperties(request, entity);
        entity.setId(id);
        entity.setUpdateTime(null); // 让数据库自动更新

        this.updateById(entity);
        return convertToResponse(entity);
    }

    @Override
    public void deleteConfig(Long id) {
        // 检查配置是否存在
        ConfigEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "配置不存在");
        }

        // 删除配置
        this.removeById(id);
    }

    @Override
    public String getConfigValue(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }

        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<ConfigEntity>()
                .eq(ConfigEntity::getConfigKey, configKey);
        
        ConfigEntity entity = this.getOne(queryWrapper);
        return entity != null ? entity.getConfigValue() : null;
    }

    /**
     * 将实体转换为响应DTO
     * @param entity 配置实体
     * @return 配置响应
     */
    private ConfigResponse convertToResponse(ConfigEntity entity) {
        if (entity == null) {
            return null;
        }

        ConfigResponse response = new ConfigResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}