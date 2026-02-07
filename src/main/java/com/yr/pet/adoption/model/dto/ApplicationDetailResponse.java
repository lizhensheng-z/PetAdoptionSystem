package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请详情响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class ApplicationDetailResponse {

    private Long id;
    private Long petId;
    private Long userId;
    private PetInfo pet;
    private OrgInfo org;
    private String status;
    private LocalDateTime submitTime;
    private QuestionnaireRequest questionnaire;
    private List<FlowHistoryResponse> flowHistory;
    private String rejectReason;
    private String orgRemark;
    private LocalDateTime decidedTime;
    private Boolean canCancel;

    // Getters and Setters
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PetInfo getPet() {
        return pet;
    }

    public void setPet(PetInfo pet) {
        this.pet = pet;
    }

    public OrgInfo getOrg() {
        return org;
    }

    public void setOrg(OrgInfo org) {
        this.org = org;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public QuestionnaireRequest getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireRequest questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<FlowHistoryResponse> getFlowHistory() {
        return flowHistory;
    }

    public void setFlowHistory(List<FlowHistoryResponse> flowHistory) {
        this.flowHistory = flowHistory;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getOrgRemark() {
        return orgRemark;
    }

    public void setOrgRemark(String orgRemark) {
        this.orgRemark = orgRemark;
    }

    public LocalDateTime getDecidedTime() {
        return decidedTime;
    }

    public void setDecidedTime(LocalDateTime decidedTime) {
        this.decidedTime = decidedTime;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }
}