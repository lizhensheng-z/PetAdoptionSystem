package com.yr.pet.adoption.model.dto;

/**
 * 最近申请项DTO
 * @author yr
 * @since 2026-02-15
 */
public class RecentApplicationItem {

    private Long id;
    private Long petId;
    private String petName;
    private String petCoverUrl;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String status;
    private String statusDesc;
    private String submitTime;
    private Object questionnaire; // 灵活存储问卷数据

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

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public Object getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Object questionnaire) {
        this.questionnaire = questionnaire;
    }
}