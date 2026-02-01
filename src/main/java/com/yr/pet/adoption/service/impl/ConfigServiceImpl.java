package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.ConfigEntity;
import com.yr.pet.adoption.mapper.ConfigMapper;
import com.yr.pet.adoption.service.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
