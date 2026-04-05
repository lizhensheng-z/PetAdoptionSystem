package com.yr.pet.adoption.model.dto;

/**
 * 回访提醒项DTO
 * @author yr
 * @since 2026-02-15
 */
public class FollowupReminderItem {

    private Long id;
    private Long petId;
    private String petName;
    private String petCoverUrl;
    private Long adoptionApplicationId;
    private String adoptedTime;
    private Long userId;
    private String userName;
    private String userPhone;
    private String lastFollowupTime;
    private String nextFollowupDate;
    private Integer overdueDays;
    private String status; // overdue, soon

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

    public String getPetCoverUrl() {
        return petCoverUrl;
    }

    public void setPetCoverUrl(String petCoverUrl) {
        this.petCoverUrl = petCoverUrl;
    }

    public Long getAdoptionApplicationId() {
        return adoptionApplicationId;
    }

    public void setAdoptionApplicationId(Long adoptionApplicationId) {
        this.adoptionApplicationId = adoptionApplicationId;
    }

    public String getAdoptedTime() {
        return adoptedTime;
    }

    public void setAdoptedTime(String adoptedTime) {
        this.adoptedTime = adoptedTime;
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

    public String getLastFollowupTime() {
        return lastFollowupTime;
    }

    public void setLastFollowupTime(String lastFollowupTime) {
        this.lastFollowupTime = lastFollowupTime;
    }

    public String getNextFollowupDate() {
        return nextFollowupDate;
    }

    public void setNextFollowupDate(String nextFollowupDate) {
        this.nextFollowupDate = nextFollowupDate;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}