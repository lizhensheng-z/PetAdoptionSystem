package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 申请取消请求DTO
 * @author yr
 * @since 2024-01-01
 */
public class ApplicationCancelRequest {

    @JsonProperty("reason")
    private String reason;

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}