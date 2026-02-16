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

    // ==================== 机构首页Dashboard接口 ====================

    /**
     * 获取机构首页统计数据
     */
    DashboardStatisticsResponse getDashboardStatistics(Long userId);

    /**
     * 获取机构待办事项列表
     */
    TodoListResponse getDashboardTodos(Long userId, String type, Integer limit);

    /**
     * 获取机构最近宠物列表
     */
    RecentPetListResponse getRecentPets(Long userId, Integer limit);

    /**
     * 获取机构最近申请列表
     */
    RecentApplicationListResponse getRecentApplications(Long userId, Integer limit);

    /**
     * 获取机构回访提醒列表
     */
    FollowupReminderListResponse getFollowupReminderList(Long userId, String status, Integer limit);

    /**
     * 获取机构首页综合数据
     */
    DashboardHomeResponse getDashboardHome(Long userId);
}
