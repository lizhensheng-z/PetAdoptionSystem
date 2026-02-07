package com.yr.pet.adoption.model.dto;

/**
 * 用户信用响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class UserCreditResponse {

    private Integer score;
    private Integer level;
    private Integer recentCheckinCount;
    private Double applicationSuccessRate;
    private Integer violations;

    // Getters and Setters
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getRecentCheckinCount() {
        return recentCheckinCount;
    }

    public void setRecentCheckinCount(Integer recentCheckinCount) {
        this.recentCheckinCount = recentCheckinCount;
    }

    public Double getApplicationSuccessRate() {
        return applicationSuccessRate;
    }

    public void setApplicationSuccessRate(Double applicationSuccessRate) {
        this.applicationSuccessRate = applicationSuccessRate;
    }

    public Integer getViolations() {
        return violations;
    }

    public void setViolations(Integer violations) {
        this.violations = violations;
    }
}