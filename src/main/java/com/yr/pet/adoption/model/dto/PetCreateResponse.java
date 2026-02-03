package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 创建宠物档案响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class PetCreateResponse {

    private Long id;
    private String status;
    private String auditStatus;
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}