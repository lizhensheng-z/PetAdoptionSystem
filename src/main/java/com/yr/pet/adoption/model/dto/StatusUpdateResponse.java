package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 状态更新响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class StatusUpdateResponse {

    private Long applicationId;
    private String fromStatus;
    private String toStatus;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}