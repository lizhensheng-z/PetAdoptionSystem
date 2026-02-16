package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.TagEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 标签字典表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface TagService extends IService<TagEntity> {

    /**
     * 检查标签名称是否已存在
     * @param name 标签名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查标签名称是否已存在（排除指定ID）
     * @param name 标签名称
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    boolean existsByNameExcludeId(String name, Long excludeId);
}
