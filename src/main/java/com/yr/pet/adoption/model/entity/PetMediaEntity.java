package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 宠物媒体表（图/视频）
 * @author yr
 * @since 2024-01-01
 */
@Data
@TableName("pet_media")
public class PetMediaEntity implements Serializable {

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
     * 媒体URL
     */
    @TableField("url")
    private String url;

    /**
     * 媒体类型：IMAGE/VIDEO
     */
    @TableField("media_type")
    private String mediaType;

    /**
     * 排序号（越小越靠前）
     */
    @TableField("sort")
    private Integer sort;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
}