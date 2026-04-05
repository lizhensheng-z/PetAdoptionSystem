package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 领养申请表
 * @author yr
 * @since 2026-01-01
 */
@TableName("adoption_application")
public class AdoptionApplicationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宠物ID（pet.id）
     */
    @TableField("pet_id")
    private Long petId;

    /**
     * 领养人用户ID（sys_user.id，role=USER）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 申请问卷JSON
     */
    @TableField("questionnaire_json")
    private String questionnaireJson;

    /**
     * 申请状态：SUBMITTED/UNDER_REVIEW/INTERVIEW/HOME_VISIT/APPROVED/REJECTED/CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 拒绝原因（机构填写）
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 机构备注
     */
    @TableField("org_remark")
    private String orgRemark;

    /**
     * 面谈时间
     */
    @TableField("interview_time")
    private LocalDateTime interviewTime;

    /**
     * 面谈地点
     */
    @TableField("interview_location")
    private String interviewLocation;

    /**
     * 最终决定时间（通过/拒绝）
     */
    @TableField("decided_time")
    private LocalDateTime decidedTime;

    /**
     * 逻辑删除：0否1是
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

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

    public String getQuestionnaireJson() {
        return questionnaireJson;
    }

    public void setQuestionnaireJson(String questionnaireJson) {
        this.questionnaireJson = questionnaireJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalDateTime interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public LocalDateTime getDecidedTime() {
        return decidedTime;
    }

    public void setDecidedTime(LocalDateTime decidedTime) {
        this.decidedTime = decidedTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}