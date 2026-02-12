package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 回访提醒DTO
 * @author yr
 * @since 2024-01-01
 */
public class FollowupReminder {
    
    private Long petId;
    private String petName;
    private Long userId;
    private String userName;
    private LocalDateTime adoptedTime;
    private Integer daysSinceAdoption;
    private Integer expectedCheckinDay;
    private Boolean isOverdue;
    private Integer daysRemaining;
    private String riskLevel;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getAdoptedTime() {
        return adoptedTime;
    }

    public void setAdoptedTime(LocalDateTime adoptedTime) {
        this.adoptedTime = adoptedTime;
    }

    public Integer getDaysSinceAdoption() {
        return daysSinceAdoption;
    }

    public void setDaysSinceAdoption(Integer daysSinceAdoption) {
        this.daysSinceAdoption = daysSinceAdoption;
    }

    public Integer getExpectedCheckinDay() {
        return expectedCheckinDay;
    }

    public void setExpectedCheckinDay(Integer expectedCheckinDay) {
        this.expectedCheckinDay = expectedCheckinDay;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}