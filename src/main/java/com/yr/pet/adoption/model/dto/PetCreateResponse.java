package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 创建宠物档案响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class PetCreateResponse {

    private Long id;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime publishedTime;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(LocalDateTime publishedTime) {
        this.publishedTime = publishedTime;
    }
}