package com.yr.pet.adoption.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 宠物媒体关联请求DTO
 * @author yr
 * @since 2026-02-15
 */
public class PetMediaRequest {

    @NotBlank(message = "媒体URL不能为空")
    private String url;

    @NotBlank(message = "媒体类型不能为空")
    private String mediaType;

    @NotNull(message = "排序号不能为空")
    private Integer sort;

    private Boolean isCover = false;

    // Getters and Setters
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

    public Boolean getIsCover() {
        return isCover;
    }

    public void setIsCover(Boolean isCover) {
        this.isCover = isCover;
    }
}