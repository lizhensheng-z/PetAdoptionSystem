package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.adoption.model.entity.AdoptionFlowLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养申请流程日志Mapper接口
 * @author yr
 * @since 2024-01-01
 */
@Mapper
public interface AdoptionFlowLogMapper extends BaseMapper<AdoptionFlowLogEntity> {
}