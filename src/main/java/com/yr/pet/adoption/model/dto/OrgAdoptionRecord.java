package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 领养完成记录响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class OrgAdoptionRecord {
    
    private Long id;
    private Long petId;
    private String petName;
    private Long userId;
    private String userName;
    private String userPhone;
    private UserCreditInfo userCredit;
    private LocalDateTime adoptedTime;
    private Integer daysSinceAdoption;
    private Integer checkinCount;
    private Double checkinRate;
    private String userStatus;
    private String riskLevel;

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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public UserCreditInfo getUserCredit() {
        return userCredit;
    }

    public void setUserCredit(UserCreditInfo userCredit) {
        this.userCredit = userCredit;
    }

    public LocalDateTime getAdoptedTime() {
        return adoptedTime;
    }

    public void setAdoptedTime(LocalDateTime adoptedTime) {
        this.adoptedTime = adoptedTime;
    }

    public Integer getDaysSinceAdoption() {
        return daysSinceAdoption;
    }

    public void setDaysSinceAdoption(Integer daysSinceAdoption) {
        this.daysSinceAdoption = daysSinceAdoption;
    }

    public Integer getCheckinCount() {
        return checkinCount;
    }

    public void setCheckinCount(Integer checkinCount) {
        this.checkinCount = checkinCount;
    }

    public Double getCheckinRate() {
        return checkinRate;
    }

    public void setCheckinRate(Double checkinRate) {
        this.checkinRate = checkinRate;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}