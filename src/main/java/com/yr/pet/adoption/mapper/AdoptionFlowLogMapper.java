package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.AdoptionFlowLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 申请状态流转日志表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface AdoptionFlowLogMapper extends BaseMapper<AdoptionFlowLogEntity> {

}
