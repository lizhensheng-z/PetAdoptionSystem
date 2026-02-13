package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.UserBehaviorMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.model.dto.BehaviorRecordRequest;
import com.yr.pet.adoption.model.entity.UserBehaviorEntity;
import com.yr.pet.adoption.model.entity.PetEntity;
import com.yr.pet.adoption.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * <p>
 * 用户行为埋点表（推荐数据源） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehaviorEntity> implements UserBehaviorService {

    @Autowired
    private PetMapper petMapper;

    private static final Map<String, Integer> BEHAVIOR_WEIGHTS = Map.of(
            "VIEW", 1,
            "FAVORITE", 3,
            "APPLY", 5,
            "SHARE", 2,
            "CHECKIN", 2
    );

    @Override
    public void recordBehavior(Long userId, BehaviorRecordRequest request) {
        // 验证宠物是否存在
        PetEntity pet = petMapper.selectById(request.getPetId());
        if (pet == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        // 验证行为类型
        String behaviorType = request.getBehaviorType().toUpperCase();
        if (!BEHAVIOR_WEIGHTS.containsKey(behaviorType)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "无效的行为类型");
        }

        // 检查今天是否已有相同行为记录（避免重复）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        LambdaQueryWrapper<UserBehaviorEntity> query = new LambdaQueryWrapper<UserBehaviorEntity>()
                .eq(UserBehaviorEntity::getUserId, userId)
                .eq(UserBehaviorEntity::getPetId, request.getPetId())
                .eq(UserBehaviorEntity::getBehaviorType, behaviorType)
                .between(UserBehaviorEntity::getCreateTime, todayStart, todayEnd);

        UserBehaviorEntity existing = this.getOne(query);
        
        if (existing != null) {
            // 已存在则增加权重
            existing.setWeight(existing.getWeight() + BEHAVIOR_WEIGHTS.get(behaviorType));
            this.updateById(existing);
        } else {
            // 创建新记录
            UserBehaviorEntity behavior = new UserBehaviorEntity();
            behavior.setUserId(userId);
            behavior.setPetId(request.getPetId());
            behavior.setBehaviorType(behaviorType);
            behavior.setWeight(BEHAVIOR_WEIGHTS.get(behaviorType));
            this.save(behavior);
        }
    }
}
