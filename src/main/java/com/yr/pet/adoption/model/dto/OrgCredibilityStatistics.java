package com.yr.pet.adoption.model.dto;

/**
 * 机构信用统计DTO
 * @author yr
 * @since 2026-01-01
 */
public class OrgCredibilityStatistics {
    
    private Integer averageUserCreditScore;
    private Integer userViolations;
    private Double orgRating;
    private Integer reviewCount;

    public Integer getAverageUserCreditScore() {
        return averageUserCreditScore;
    }

    public void setAverageUserCreditScore(Integer averageUserCreditScore) {
        this.averageUserCreditScore = averageUserCreditScore;
    }

    public Integer getUserViolations() {
        return userViolations;
    }

    public void setUserViolations(Integer userViolations) {
        this.userViolations = userViolations;
    }

    public Double getOrgRating() {
        return orgRating;
    }

    public void setOrgRating(Double orgRating) {
        this.orgRating = orgRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}