package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class CheckinResponse {

    private Long id;
    private Long petId;
    private Long userId;
    private LocalDateTime createTime;
    private Integer creditDelta;
    private String creditReason;
    private Integer creditScore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getCreditDelta() {
        return creditDelta;
    }

    public void setCreditDelta(Integer creditDelta) {
        this.creditDelta = creditDelta;
    }

    public String getCreditReason() {
        return creditReason;
    }

    public void setCreditReason(String creditReason) {
        this.creditReason = creditReason;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
}