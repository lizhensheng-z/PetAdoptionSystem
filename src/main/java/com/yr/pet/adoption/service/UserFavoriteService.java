package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.UserFavoriteEntity;
import com.yr.pet.adoption.model.dto.FavoriteListItem;
import com.yr.pet.adoption.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface UserFavoriteService extends IService<UserFavoriteEntity> {

    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long petId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long petId);

    /**
     * 获取我的收藏列表
     */
    PageResult<FavoriteListItem> getMyFavorites(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long petId);
}
