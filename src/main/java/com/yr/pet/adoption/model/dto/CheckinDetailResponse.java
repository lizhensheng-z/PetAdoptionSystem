package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡详情响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class CheckinDetailResponse {

    private Long id;
    private Long petId;
    private PetSimpleInfo pet;
    private Long userId;
    private UserSimpleInfo user;
    private OrgSimpleInfo org;
    private String content;
    private List<String> mediaUrls;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer likes;
    private Integer comments;
    private Boolean isLiked;
    private Boolean canEdit;
    private Boolean canDelete;

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

    public PetSimpleInfo getPet() {
        return pet;
    }

    public void setPet(PetSimpleInfo pet) {
        this.pet = pet;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserSimpleInfo getUser() {
        return user;
    }

    public void setUser(UserSimpleInfo user) {
        this.user = user;
    }

    public OrgSimpleInfo getOrg() {
        return org;
    }

    public void setOrg(OrgSimpleInfo org) {
        this.org = org;
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

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public static class PetSimpleInfo {
        private Long id;
        private String name;
        private String species;
        private String coverUrl;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpecies() {
            return species;
        }

        public void setSpecies(String species) {
            this.species = species;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }
    }

    public static class UserSimpleInfo {
        private Long id;
        private String username;
        private String avatar;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class OrgSimpleInfo {
        private Long id;
        private String orgName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }
    }
}