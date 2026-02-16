package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 救助机构资料表 Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface OrgProfileMapper extends BaseMapper<OrgProfileEntity> {
    @Select("select * from org_profile where user_id = #{orgUserId}")
    OrgProfileEntity selectByUserId(@Param("orgUserId") Long orgUserId);
}
