package com.yr.pet.adoption.mapper;

import com.yr.pet.adoption.model.entity.PermissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 权限表（API/菜单/按钮） Mapper 接口
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

}
