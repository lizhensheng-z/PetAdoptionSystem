package com.yr.pet.adoption.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * 行为记录请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class BehaviorRecordRequest {

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @NotBlank(message = "行为类型不能为空")
    private String behaviorType;

    private Map<String, Object> metadata;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}