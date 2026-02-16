package com.yr.pet.adoption.model.dto;

/**
 * 待办事项DTO
 * @author yr
 * @since 2024-02-15
 */
public class TodoItem {

    private Long id;
    private String type; // application, followup, audit
    private String title;
    private String petName;
    private Long petId;
    private String petCoverUrl;
    private String userName;
    private Long userId;
    private String userAvatar;
    private String status;
    private String submitTime;
    private String priority; // high, medium, urgent, low
    private String adoptionTime;
    private Integer overdueDays;
    private String lastFollowupTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetCoverUrl() {
        return petCoverUrl;
    }

    public void setPetCoverUrl(String petCoverUrl) {
        this.petCoverUrl = petCoverUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAdoptionTime() {
        return adoptionTime;
    }

    public void setAdoptionTime(String adoptionTime) {
        this.adoptionTime = adoptionTime;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getLastFollowupTime() {
        return lastFollowupTime;
    }

    public void setLastFollowupTime(String lastFollowupTime) {
        this.lastFollowupTime = lastFollowupTime;
    }
}