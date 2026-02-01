package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签字典表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

}
