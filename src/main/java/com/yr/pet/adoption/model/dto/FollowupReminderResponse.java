package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 回访提醒响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class FollowupReminderResponse {
    
    private List<FollowupReminder> overdue;
    private List<FollowupReminder> upcoming;

    public List<FollowupReminder> getOverdue() {
        return overdue;
    }

    public void setOverdue(List<FollowupReminder> overdue) {
        this.overdue = overdue;
    }

    public List<FollowupReminder> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<FollowupReminder> upcoming) {
        this.upcoming = upcoming;
    }
}