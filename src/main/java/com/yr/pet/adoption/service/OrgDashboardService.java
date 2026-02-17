package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.*;

import java.util.List;

/**
 * 机构管理首页服务接口
 * @author 宗平
 * @since 2026-02-17
 */
public interface OrgDashboardService {

    /**
     * 获取机构统计数据
     * @param orgUserId 机构用户ID
     * @return 统计数据
     */
    OrgStatisticsResponse getStatistics(Long orgUserId);

    /**
     * 获取待办事项列表
     * @param orgUserId 机构用户ID
     * @param type 待办类型筛选
     * @param limit 返回数量限制
     * @return 待办事项列表
     */
    TodoListResponse getTodos(Long orgUserId, String type, Integer limit);

    /**
     * 获取最近宠物列表
     * @param orgUserId 机构用户ID
     * @param limit 返回数量限制
     * @return 最近宠物列表
     */
    List<PetListResponse> getRecentPets(Long orgUserId, Integer limit);

    /**
     * 获取最新申请列表
     * @param orgUserId 机构用户ID
     * @param limit 返回数量限制
     * @return 最新申请列表
     */
    List<ApplicationListResponse> getRecentApplications(Long orgUserId, Integer limit);

    /**
     * 获取回访提醒列表
     * @param orgUserId 机构用户ID
     * @param status 回访状态筛选
     * @param limit 返回数量限制
     * @return 回访提醒列表
     */
    FollowupReminderListResponse getFollowupReminders(Long orgUserId, String status, Integer limit);

    /**
     * 获取首页综合数据
     * @param orgUserId 机构用户ID
     * @return 首页综合数据
     */
    OrgDashboardHomeResponse getHomeData(Long orgUserId);
}