package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.ConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.ConfigRequest;
import com.yr.pet.adoption.model.dto.ConfigResponse;

/**
 * <p>
 * 系统配置表（推荐权重/信用规则等） 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface ConfigService extends IService<ConfigEntity> {

    /**
     * 获取配置列表（分页）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @param configKey 配置键（模糊搜索）
     * @param remark 备注（模糊搜索）
     * @return 分页结果
     */
    PageResult<ConfigResponse> listConfigs(Integer pageNo, Integer pageSize, String configKey, String remark);

    /**
     * 创建配置
     * @param request 配置请求
     * @return 配置响应
     */
    ConfigResponse createConfig(ConfigRequest request);

    /**
     * 更新配置
     * @param id 配置ID
     * @param request 配置请求
     * @return 配置响应
     */
    ConfigResponse updateConfig(Long id, ConfigRequest request);

    /**
     * 删除配置
     * @param id 配置ID
     */
    void deleteConfig(Long id);

    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);
}