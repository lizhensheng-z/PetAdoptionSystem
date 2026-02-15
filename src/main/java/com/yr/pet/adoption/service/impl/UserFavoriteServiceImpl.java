package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.model.dto.FavoriteListItem;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.service.UserFavoriteService;
import com.yr.pet.adoption.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavoriteEntity> implements UserFavoriteService {

    @Autowired
    private PetMapper petMapper;
    @Autowired
    private OrgProfileMapper orgProfileMapper;
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long petId) {
        if (userId == null || petId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }

        // 检查宠物是否存在
        PetEntity pet = petMapper.selectById(petId);
        if (pet == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        // 检查是否已收藏（物理查询，只查未删除的）
        boolean alreadyFavorited = this.lambdaQuery()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId)
                .count() > 0;

        if (alreadyFavorited) {
            throw new BizException(ErrorCode.DUPLICATE_OPERATION, "已收藏该宠物");
        }

        // 新增收藏记录（物理新增）
        UserFavoriteEntity favorite = new UserFavoriteEntity();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        favorite.setDeleted((byte) 0); // 保留字段但不使用逻辑删除
        this.save(favorite);

        recordBehavior(userId, petId, "FAVORITE", 3);
        updatePetFavoriteCount(petId, 1);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long petId) {
        if (userId == null || petId == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }

        // 物理删除收藏记录
        boolean removed = this.lambdaUpdate()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId)
                .remove();
        
        if (removed) {
            // 更新宠物收藏数
            updatePetFavoriteCount(petId, -1);
            
            // 记录用户行为
            recordBehavior(userId, petId, "UNFAVORITE", -2);
        }
    }

    @Override
    public PageResult<FavoriteListItem> getMyFavorites(Long userId, Integer pageNo, Integer pageSize) {
        if (userId == null || pageNo == null || pageSize == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }

        // 查询收藏记录（物理查询，不再过滤deleted字段）
        Page<UserFavoriteEntity> page = this.lambdaQuery()
                .eq(UserFavoriteEntity::getUserId, userId)
                .orderByDesc(UserFavoriteEntity::getCreateTime)
                .page(new Page<>(pageNo, pageSize));

        if (page.getRecords().isEmpty()) {
        return new PageResult<FavoriteListItem>(new ArrayList<>(), pageNo, pageSize, 0L);
        }

        // 获取宠物ID列表
        List<Long> petIds = page.getRecords().stream()
                .map(UserFavoriteEntity::getPetId)
                .collect(Collectors.toList());

        // 批量查询宠物信息
        List<PetEntity> pets = petMapper.selectBatchIds(petIds);
        if (pets.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), pageNo, pageSize, 0L);
        }

        Map<Long, PetEntity> petMap = pets.stream()
                .collect(Collectors.toMap(PetEntity::getId, p -> p));

        // 批量查询机构信息
        List<Long> orgUserIds = pets.stream()
                .map(PetEntity::getOrgUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> orgNameMap = orgUserIds.isEmpty() ? Map.of() :
                orgProfileMapper.selectBatchIds(orgUserIds).stream()
                        .collect(Collectors.toMap(OrgProfileEntity::getUserId, OrgProfileEntity::getOrgName));

        // 构建返回结果
        List<FavoriteListItem> list = page.getRecords().stream()
                .map(entity -> {
                    FavoriteListItem item = new FavoriteListItem();
                    item.setId(entity.getId());
                    item.setFavoritedTime(entity.getCreateTime());

                    PetEntity pet = petMap.get(entity.getPetId());
                    if (pet != null) {
                        FavoriteListItem.PetSimpleInfo petInfo = new FavoriteListItem.PetSimpleInfo();
                        petInfo.setId(pet.getId());
                        petInfo.setName(pet.getName());
                        petInfo.setSpecies(pet.getSpecies());
                        petInfo.setCoverUrl(pet.getCoverUrl());
                        petInfo.setStatus(pet.getStatus());
                        petInfo.setOrgName(orgNameMap.get(pet.getOrgUserId()));
                        item.setPet(petInfo);
                    }
                    return item;
                })
                .collect(Collectors.toList());

        return new PageResult<FavoriteListItem>(list, pageNo, pageSize, page.getTotal());
    }

    @Override
    public boolean isFavorited(Long userId, Long petId) {
        if (userId == null || petId == null) {
            return false;
        }
        
        return this.lambdaQuery()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId)
                .count() > 0;
    }

    private void recordBehavior(Long userId, Long petId, String behaviorType, int weight) {
        UserBehaviorEntity behavior = new UserBehaviorEntity();
        behavior.setUserId(userId);
        behavior.setPetId(petId);
        behavior.setBehaviorType(behaviorType);
        behavior.setWeight(weight);
        userBehaviorMapper.insert(behavior);
    }

    private void updatePetFavoriteCount(Long petId, int delta) {
        // 如果pet表中有favorite_count字段，可以在这里更新
        // 这里暂时不实现，因为需要确认表结构
        // 实际实现可能类似：
        // petMapper.updateFavoriteCount(petId, delta);
    }
}
