package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.CheckinPostEntity;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 领养后打卡表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface CheckinPostService extends IService<CheckinPostEntity> {

    /**
     * 创建打卡
     */
    CheckinResponse createCheckin(Long userId, CheckinCreateRequest request);

    /**
     * 获取我的打卡列表
     */
    PageResult<CheckinListItem> getMyCheckins(Long userId, Long petId, Integer pageNo, Integer pageSize);

    /**
     * 获取打卡详情
     */
    CheckinDetailResponse getCheckinDetail(Long userId, Long checkinId);

    /**
     * 删除打卡
     */
    void deleteCheckin(Long userId, Long checkinId);
}
