package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.AuditLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审计日志表（关键操作留痕） Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {

}
