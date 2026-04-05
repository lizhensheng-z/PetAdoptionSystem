package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 宠物档案表
 * @author yr
 * @since 2026-01-01
 */
@TableName("pet")
public class PetEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布机构用户ID（sys_user.id，role=ORG）
     */
    @TableField("org_user_id")
    private Long orgUserId;

    /**
     * 宠物名字/昵称
     */
    @TableField("name")
    private String name;

    /**
     * 物种：CAT/DOG/OTHER
     */
    @TableField("species")
    private String species;

    /**
     * 品种
     */
    @TableField("breed")
    private String breed;

    /**
     * 性别：MALE/FEMALE/UNKNOWN
     */
    @TableField("gender")
    private String gender;

    /**
     * 年龄（月）
     */
    @TableField("age_month")
    private Integer ageMonth;

    /**
     * 体型：S/M/L
     */
    @TableField("size")
    private String size;

    /**
     * 毛色/颜色
     */
    @TableField("color")
    private String color;

    /**
     * 是否绝育：0否1是
     */
    @TableField("sterilized")
    private Boolean sterilized;

    /**
     * 是否疫苗：0否1是
     */
    @TableField("vaccinated")
    private Boolean vaccinated;

    /**
     * 是否驱虫：0否1是
     */
    @TableField("dewormed")
    private Boolean dewormed;

    /**
     * 健康描述
     */
    @TableField("health_desc")
    private String healthDesc;

    /**
     * 性格描述（文本）
     */
    @TableField("personality_desc")
    private String personalityDesc;

    /**
     * 领养要求（文本）
     */
    @TableField("adopt_requirements")
    private String adoptRequirements;

    /**
     * 宠物状态：PUBLISHED(已发布)/ADOPTED(已领养)
     */
    @TableField("status")
    private String status;

    /**
     * 经度（默认继承机构坐标）
     */
    @TableField("lng")
    private BigDecimal lng;

    /**
     * 纬度（默认继承机构坐标）
     */
    @TableField("lat")
    private BigDecimal lat;

    /**
     * 封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 发布时间（审核通过后写入）
     */
    @TableField("published_time")
    private LocalDateTime publishedTime;

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

    public Long getOrgUserId() {
        return orgUserId;
    }

    public void setOrgUserId(Long orgUserId) {
        this.orgUserId = orgUserId;
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