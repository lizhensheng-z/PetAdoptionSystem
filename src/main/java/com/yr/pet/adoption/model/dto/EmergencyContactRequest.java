package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;


/**
 * 紧急联系人DTO
 * @author yr
 * @since 2026-01-01
 */
public class EmergencyContactRequest {

    @NotEmpty(message = "联系人姓名不能为空")
    @JsonProperty("name")
    private String name;

    @NotEmpty(message = "联系人电话不能为空")
    @JsonProperty("phone")
    private String phone;

    @NotEmpty(message = "与申请人关系不能为空")
    @JsonProperty("relationship")
    private String relationship;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}