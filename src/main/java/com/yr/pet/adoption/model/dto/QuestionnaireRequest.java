package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 问卷内容DTO
 * @author yr
 * @since 2026-01-01
 */
@Data
public class QuestionnaireRequest {

    @NotNull(message = "是否和家人同住不能为空")
    @JsonProperty("liveWithFamily")
    private Boolean liveWithFamily;

    @NotEmpty(message = "家庭成员描述不能为空")
    @JsonProperty("familyMembers")
    private String familyMembers;

    @NotEmpty(message = "住房类型不能为空")
    @JsonProperty("housingType")
    private String housingType;

    @NotEmpty(message = "住房所有不能为空")
    @JsonProperty("housingOwner")
    private String housingOwner;

    @NotNull(message = "是否有养宠物经验不能为空")
    @JsonProperty("petRaisingExp")
    private Boolean petRaisingExp;

    @JsonProperty("petRaisingExpDesc")
    private String petRaisingExpDesc;

    @NotEmpty(message = "工作时间表不能为空")
    @JsonProperty("workSchedule")
    private String workSchedule;

    @NotEmpty(message = "月收入范围不能为空")
    @JsonProperty("monthlyIncome")
    private String monthlyIncome;

    @NotEmpty(message = "领养原因不能为空")
    @JsonProperty("adoptionReason")
    private String adoptionReason;

    @NotEmpty(message = "领养承诺不能为空")
    @JsonProperty("adoptionCommitment")
    private String adoptionCommitment;

    @JsonProperty("photos")
    private List<String> photos;

    @NotNull(message = "紧急联系人不能为空")
    @JsonProperty("emergencyContact")
    private EmergencyContactRequest emergencyContact;

    // Getters and Setters
    public Boolean getLiveWithFamily() {
        return liveWithFamily;
    }

    public void setLiveWithFamily(Boolean liveWithFamily) {
        this.liveWithFamily = liveWithFamily;
    }

    public String getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(String familyMembers) {
        this.familyMembers = familyMembers;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public String getHousingOwner() {
        return housingOwner;
    }

    public void setHousingOwner(String housingOwner) {
        this.housingOwner = housingOwner;
    }

    public Boolean getPetRaisingExp() {
        return petRaisingExp;
    }

    public void setPetRaisingExp(Boolean petRaisingExp) {
        this.petRaisingExp = petRaisingExp;
    }

    public String getPetRaisingExpDesc() {
        return petRaisingExpDesc;
    }

    public void setPetRaisingExpDesc(String petRaisingExpDesc) {
        this.petRaisingExpDesc = petRaisingExpDesc;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getAdoptionReason() {
        return adoptionReason;
    }

    public void setAdoptionReason(String adoptionReason) {
        this.adoptionReason = adoptionReason;
    }

    public String getAdoptionCommitment() {
        return adoptionCommitment;
    }

    public void setAdoptionCommitment(String adoptionCommitment) {
        this.adoptionCommitment = adoptionCommitment;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public EmergencyContactRequest getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(EmergencyContactRequest emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}