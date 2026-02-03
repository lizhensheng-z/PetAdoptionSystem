package com.yr.pet.adoption.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物详情响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class PetDetailResponse {

    private Long id;
    private Long orgUserId;
    private String orgName;
    private OrgProfileResponse orgProfile;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private Integer ageMonth;
    private String size;
    private String color;
    private Boolean sterilized;
    private Boolean vaccinated;
    private Boolean dewormed;
    private String healthDesc;
    private String personalityDesc;
    private String adoptRequirements;
    private String status;
    private String auditStatus;
    private String coverUrl;
    private LocalDateTime publishedTime;
    private LocalDateTime createTime;
    private List<TagResponse> tags;
    private List<PetMediaResponse> media;
    private LocationResponse location;
    private PetStatisticsResponse statistics;
    private ApplicationStatusResponse applicationStatus;
    private Boolean isFavorited;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgUserId() {
        return orgUserId;
    }

    public void setOrgUserId(Long orgUserId) {
        this.orgUserId = orgUserId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public OrgProfileResponse getOrgProfile() {
        return orgProfile;
    }

    public void setOrgProfile(OrgProfileResponse orgProfile) {
        this.orgProfile = orgProfile;
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

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAgeMonth() {
        return ageMonth;
    }

    public void setAgeMonth(Integer ageMonth) {
        this.ageMonth = ageMonth;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getSterilized() {
        return sterilized;
    }

    public void setSterilized(Boolean sterilized) {
        this.sterilized = sterilized;
    }

    public Boolean getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public Boolean getDewormed() {
        return dewormed;
    }

    public void setDewormed(Boolean dewormed) {
        this.dewormed = dewormed;
    }

    public String getHealthDesc() {
        return healthDesc;
    }

    public void setHealthDesc(String healthDesc) {
        this.healthDesc = healthDesc;
    }

    public String getPersonalityDesc() {
        return personalityDesc;
    }

    public void setPersonalityDesc(String personalityDesc) {
        this.personalityDesc = personalityDesc;
    }

    public String getAdoptRequirements() {
        return adoptRequirements;
    }

    public void setAdoptRequirements(String adoptRequirements) {
        this.adoptRequirements = adoptRequirements;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public LocalDateTime getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(LocalDateTime publishedTime) {
        this.publishedTime = publishedTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<TagResponse> getTags() {
        return tags;
    }

    public void setTags(List<TagResponse> tags) {
        this.tags = tags;
    }

    public List<PetMediaResponse> getMedia() {
        return media;
    }

    public void setMedia(List<PetMediaResponse> media) {
        this.media = media;
    }

    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }

    public PetStatisticsResponse getStatistics() {
        return statistics;
    }

    public void setStatistics(PetStatisticsResponse statistics) {
        this.statistics = statistics;
    }

    public ApplicationStatusResponse getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatusResponse applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}