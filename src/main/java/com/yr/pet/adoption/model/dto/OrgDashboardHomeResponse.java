package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 机构首页综合数据响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "机构首页综合数据响应")
public class OrgDashboardHomeResponse {
    
    @Schema(description = "统计数据")
    private OrgStatisticsResponse statistics;
    
    @Schema(description = "待办事项列表")
    private List<TodoItemResponse> todos;
    
    @Schema(description = "最近宠物列表")
    private List<PetListResponse> recentPets;
    
    @Schema(description = "最近申请列表")
    private List<ApplicationListResponse> recentApplications;
    
    @Schema(description = "回访提醒列表")
    private List<FollowupReminderResponse> followupReminders;
    
    @Schema(description = "机构基本信息")
    private OrgProfileResponse orgInfo;
}