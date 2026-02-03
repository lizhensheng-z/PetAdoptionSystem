package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 提交审核响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class PetAuditResponse {

    private Long petId;
    private Long auditId;
    private String status;
    private String auditStatus;
    private LocalDateTime submitTime;

    // Getters and Setters
    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }
}