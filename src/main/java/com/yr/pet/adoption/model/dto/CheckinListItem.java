package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡列表项DTO
 * @author yr
 * @since 2026-01-01
 */
public class CheckinListItem {

    private Long id;
    private Long petId;
    private String petName;
    private String petCoverUrl;
    private String orgName;
    private String content;
    private List<String> mediaUrls;
    private Integer mediaCount;
    private LocalDateTime createTime;
    private Integer creditDelta;
    private Integer likes;
    private Integer comments;

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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getCreditDelta() {
        return creditDelta;
    }

    public void setCreditDelta(Integer creditDelta) {
        this.creditDelta = creditDelta;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}