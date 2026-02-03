package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建宠物档案请求DTO
 * @author yr
 * @since 2024-01-01
 */
public class PetCreateRequest {

    @Size(max = 64, message = "宠物名字不能超过64字符")
    private String name;

    @NotNull(message = "物种不能为空")
    private String species;

    @Size(max = 64, message = "品种不能超过64字符")
    private String breed;

    private String gender;

    private Integer ageMonth;

    private String size;

    @Size(max = 32, message = "毛色不能超过32字符")
    private String color;

    private Boolean sterilized;

    private Boolean vaccinated;

    private Boolean dewormed;

    @Size(max = 1000, message = "健康描述不能超过1000字")
    private String healthDesc;

    @Size(max = 1000, message = "性格描述不能超过1000字")
    private String personalityDesc;

    @Size(max = 1000, message = "领养要求不能超过1000字")
    private String adoptRequirements;

    @JsonProperty("tagIds")
    private List<Long> tagIds;

    private BigDecimal lng;

    private BigDecimal lat;

    // Getters and Setters
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

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }
}