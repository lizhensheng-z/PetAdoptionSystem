package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表（领养人/机构/管理员） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
