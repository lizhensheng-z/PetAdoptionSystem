package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 回访提醒列表响应DTO
 * @author yr
 * @since 2024-02-15
 */
public class FollowupReminderListResponse {

    private List<FollowupReminderItem> list;

    public List<FollowupReminderItem> getList() {
        return list;
    }

    public void setList(List<FollowupReminderItem> list) {
        this.list = list;
    }
}