package com.yr.pet.adoption.model.dto;

/**
 * 用户已领养宠物响应DTO
 * @author yr
 * @since 2024-02-18
 */
public class UserAdoptedPetResponse {

    private Long petId;
    private String name;
    private String coverUrl;

    public UserAdoptedPetResponse() {
    }

    public UserAdoptedPetResponse(Long petId, String name, String coverUrl) {
        this.petId = petId;
        this.name = name;
        this.coverUrl = coverUrl;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}