package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.PetAuditEntity;
import com.yr.pet.adoption.mapper.PetAuditMapper;
import com.yr.pet.adoption.service.PetAuditService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 宠物发布审核记录表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class PetAuditServiceImpl extends ServiceImpl<PetAuditMapper, PetAuditEntity> implements PetAuditService {

}
