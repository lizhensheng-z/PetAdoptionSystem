package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.UserBehaviorEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户行为埋点表（推荐数据源） Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehaviorEntity> {

}
