package com.yr.pet.adoption.model.dto;

/**
 * 机构首页统计数据响应DTO
 * @author yr
 * @since 2024-02-15
 */
public class DashboardStatisticsResponse {

    private Integer totalPets;
    private Integer pendingApplications;
    private Integer monthlyAdoptions;
    private Integer pendingFollowups;
    private Integer totalAdoptions;
    private Integer publishedPets;
    private Integer draftPets;
    private Integer underReviewPets;

    public Integer getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(Integer totalPets) {
        this.totalPets = totalPets;
    }

    public Integer getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(Integer pendingApplications) {
        this.pendingApplications = pendingApplications;
    }

    public Integer getMonthlyAdoptions() {
        return monthlyAdoptions;
    }

    public void setMonthlyAdoptions(Integer monthlyAdoptions) {
        this.monthlyAdoptions = monthlyAdoptions;
    }

    public Integer getPendingFollowups() {
        return pendingFollowups;
    }

    public void setPendingFollowups(Integer pendingFollowups) {
        this.pendingFollowups = pendingFollowups;
    }

    public Integer getTotalAdoptions() {
        return totalAdoptions;
    }

    public void setTotalAdoptions(Integer totalAdoptions) {
        this.totalAdoptions = totalAdoptions;
    }

    public Integer getPublishedPets() {
        return publishedPets;
    }

    public void setPublishedPets(Integer publishedPets) {
        this.publishedPets = publishedPets;
    }

    public Integer getDraftPets() {
        return draftPets;
    }

    public void setDraftPets(Integer draftPets) {
        this.draftPets = draftPets;
    }

    public Integer getUnderReviewPets() {
        return underReviewPets;
    }

    public void setUnderReviewPets(Integer underReviewPets) {
        this.underReviewPets = underReviewPets;
    }
}