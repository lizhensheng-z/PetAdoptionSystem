package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 领养申请响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class AdoptionApplicationResponse {

    private Long applicationId;
    private Long petId;
    private Long userId;
    private String status;
    private LocalDateTime submitTime;
    private String estimatedReviewTime;

    // Getters and Setters
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public String getEstimatedReviewTime() {
        return estimatedReviewTime;
    }

    public void setEstimatedReviewTime(String estimatedReviewTime) {
        this.estimatedReviewTime = estimatedReviewTime;
    }
}