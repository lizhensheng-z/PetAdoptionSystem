package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.UserBehaviorEntity;
import com.yr.pet.adoption.model.dto.BehaviorRecordRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户行为埋点表（推荐数据源） 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface UserBehaviorService extends IService<UserBehaviorEntity> {

    /**
     * 记录用户行为
     */
    void recordBehavior(Long userId, BehaviorRecordRequest request);
}
