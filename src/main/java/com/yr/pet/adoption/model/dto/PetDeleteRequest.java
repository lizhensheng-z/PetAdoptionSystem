package com.yr.pet.adoption.model.dto;

/**
 * 宠物删除请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class PetDeleteRequest {

    private String reason;

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}