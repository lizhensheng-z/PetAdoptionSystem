package com.yr.pet.adoption.model.dto;

/**
 * 申请状态响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class ApplicationStatusResponse {

    private Boolean canApply;
    private Long userApplicationId;
    private String userApplicationStatus;

    // Getters and Setters
    public Boolean getCanApply() {
        return canApply;
    }

    public void setCanApply(Boolean canApply) {
        this.canApply = canApply;
    }

    public Long getUserApplicationId() {
        return userApplicationId;
    }

    public void setUserApplicationId(Long userApplicationId) {
        this.userApplicationId = userApplicationId;
    }

    public String getUserApplicationStatus() {
        return userApplicationStatus;
    }

    public void setUserApplicationStatus(String userApplicationStatus) {
        this.userApplicationStatus = userApplicationStatus;
    }
}