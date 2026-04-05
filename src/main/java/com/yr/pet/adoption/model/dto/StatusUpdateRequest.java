package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

/**
 * 状态更新请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class StatusUpdateRequest {

    @NotEmpty(message = "目标状态不能为空")
    @JsonProperty("toStatus")
    private String toStatus;

    @JsonProperty("rejectReason")
    private String rejectReason;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("interviewTime")
    private LocalDateTime interviewTime;

    @JsonProperty("interviewLocation")
    private String interviewLocation;

    // Getters and Setters
    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalDateTime interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }
}