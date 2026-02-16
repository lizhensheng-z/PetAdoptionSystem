package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 机构首页综合数据响应DTO
 * @author yr
 * @since 2024-02-15
 */
public class DashboardHomeResponse {

    private DashboardStatisticsResponse statistics;
    private List<TodoItem> todos;
    private List<RecentPetItem> recentPets;
    private List<RecentApplicationItem> recentApplications;
    private List<FollowupReminderItem> followupReminders;
    private OrgInfo orgInfo;

    public DashboardStatisticsResponse getStatistics() {
        return statistics;
    }

    public void setStatistics(DashboardStatisticsResponse statistics) {
        this.statistics = statistics;
    }

    public List<TodoItem> getTodos() {
        return todos;
    }

    public void setTodos(List<TodoItem> todos) {
        this.todos = todos;
    }

    public List<RecentPetItem> getRecentPets() {
        return recentPets;
    }

    public void setRecentPets(List<RecentPetItem> recentPets) {
        this.recentPets = recentPets;
    }

    public List<RecentApplicationItem> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<RecentApplicationItem> recentApplications) {
        this.recentApplications = recentApplications;
    }

    public List<FollowupReminderItem> getFollowupReminders() {
        return followupReminders;
    }

    public void setFollowupReminders(List<FollowupReminderItem> followupReminders) {
        this.followupReminders = followupReminders;
    }

    public OrgInfo getOrgInfo() {
        return orgInfo;
    }

    public void setOrgInfo(OrgInfo orgInfo) {
        this.orgInfo = orgInfo;
    }
}