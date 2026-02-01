package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.UserRoleEntity;
import com.yr.pet.adoption.mapper.UserRoleMapper;
import com.yr.pet.adoption.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements UserRoleService {

}
