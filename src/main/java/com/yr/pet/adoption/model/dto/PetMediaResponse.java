package com.yr.pet.adoption.model.dto;

/**
 * 宠物媒体响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class PetMediaResponse {

    private Long id;
    private Long petId;
    private String url;
    private String mediaType;
    private Integer sort;

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
}