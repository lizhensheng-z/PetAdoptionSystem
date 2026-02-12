package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.model.dto.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yr.pet.adoption.common.PageResult;

/**
 * <p>
 * 救助机构资料表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface OrgProfileService extends IService<OrgProfileEntity> {

    /**
     * 获取机构资料
     */
    OrgProfileResponse getProfile(Long userId);

    /**
     * 更新机构资料
     */
    void updateProfile(Long userId, OrgProfileUpdateRequest request);

    /**
     * 获取机构统计数据
     */
    OrgStatisticsResponse getStatistics(Long userId);

    /**
     * 获取领养完成记录
     */
    PageResult<OrgAdoptionRecord> getAdoptionRecords(Long userId, Long petId, Long targetUserId, String month, Integer pageNo, Integer pageSize);

    /**
     * 获取回访提醒
     */
    FollowupReminderResponse getFollowupReminders(Long userId);
}
