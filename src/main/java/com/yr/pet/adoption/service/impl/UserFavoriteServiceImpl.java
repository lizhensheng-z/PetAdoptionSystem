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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long petId) {
        // 检查是否已收藏
        LambdaQueryWrapper<UserFavoriteEntity> query = new LambdaQueryWrapper<UserFavoriteEntity>()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId);
        
        UserFavoriteEntity existing = this.getOne(query);
        if (existing != null) {
            throw new BizException(ErrorCode.DUPLICATE_OPERATION, "已收藏该宠物");
        }

        // 检查宠物是否存在
        PetEntity pet = petMapper.selectById(petId);
        if (pet == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        // 创建收藏记录
        UserFavoriteEntity favorite = new UserFavoriteEntity();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        this.save(favorite);

        // 记录行为埋点
        recordBehavior(userId, petId, "FAVORITE", 3);

        // 更新宠物收藏数
        updatePetFavoriteCount(petId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long petId) {
        LambdaQueryWrapper<UserFavoriteEntity> query = new LambdaQueryWrapper<UserFavoriteEntity>()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId);
        
        UserFavoriteEntity existing = this.getOne(query);
        if (existing == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "收藏记录不存在");
        }

        // 逻辑删除
        this.removeById(existing.getId());

        // 更新宠物收藏数
        updatePetFavoriteCount(petId, -1);
    }

    @Override
    public PageResult<FavoriteListItem> getMyFavorites(Long userId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<UserFavoriteEntity> query = new LambdaQueryWrapper<UserFavoriteEntity>()
                .eq(UserFavoriteEntity::getUserId, userId)
                .orderByDesc(UserFavoriteEntity::getCreateTime);
        
        Page<UserFavoriteEntity> page = new Page<>(pageNo, pageSize);
        Page<UserFavoriteEntity> result = this.page(page, query);
        
        List<FavoriteListItem> list = new ArrayList<>();
        
        if (!result.getRecords().isEmpty()) {
            List<Long> petIds = result.getRecords().stream()
                    .map(UserFavoriteEntity::getPetId)
                    .collect(Collectors.toList());
            
            // 批量查询宠物信息
            List<PetEntity> pets = petMapper.selectBatchIds(petIds);
            Map<Long, PetEntity> petMap = pets.stream()
                    .collect(Collectors.toMap(PetEntity::getId, p -> p));
            
            // 批量查询机构信息
            List<Long> orgUserIds = pets.stream()
                    .map(PetEntity::getOrgUserId)
                    .distinct()
                    .collect(Collectors.toList());
            
            Map<Long, String> orgNameMap = null;
            if (!orgUserIds.isEmpty()) {
                List<OrgProfileEntity> orgs = orgProfileMapper.selectBatchIds(orgUserIds);
                orgNameMap = orgs.stream()
                        .collect(Collectors.toMap(OrgProfileEntity::getUserId, OrgProfileEntity::getOrgName));
            }
            
            for (UserFavoriteEntity entity : result.getRecords()) {
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
                    petInfo.setOrgName(orgNameMap != null ? orgNameMap.get(pet.getOrgUserId()) : null);
                    item.setPet(petInfo);
                }
                
                list.add(item);
            }
        }
        
        return new PageResult<>(list, pageNo, pageSize, result.getTotal(),
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public boolean isFavorited(Long userId, Long petId) {
        LambdaQueryWrapper<UserFavoriteEntity> query = new LambdaQueryWrapper<UserFavoriteEntity>()
                .eq(UserFavoriteEntity::getUserId, userId)
                .eq(UserFavoriteEntity::getPetId, petId);
        return this.count(query) > 0;
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
        PetEntity pet = petMapper.selectById(petId);
        if (pet != null) {
            // 注意：pet表中可能没有favorite_count字段，这里简化处理
            // 如果需要更新收藏数，需要在pet表中添加该字段
        }
    }
}
