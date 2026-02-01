package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.AuditLogEntity;
import com.yr.pet.adoption.mapper.AuditLogMapper;
import com.yr.pet.adoption.service.AuditLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审计日志表（关键操作留痕） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLogEntity> implements AuditLogService {

}
