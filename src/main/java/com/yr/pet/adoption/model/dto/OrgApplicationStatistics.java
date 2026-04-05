package com.yr.pet.adoption.model.dto;

/**
 * 机构申请统计DTO
 * @author yr
 * @since 2026-01-01
 */
public class OrgApplicationStatistics {
    
    private Integer totalApplications;
    private Integer pendingReview;
    private Integer inProgress;
    private Integer approved;
    private Integer rejected;

    public Integer getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(Integer totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Integer getPendingReview() {
        return pendingReview;
    }

    public void setPendingReview(Integer pendingReview) {
        this.pendingReview = pendingReview;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }
}