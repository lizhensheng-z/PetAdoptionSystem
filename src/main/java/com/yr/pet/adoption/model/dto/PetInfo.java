package com.yr.pet.adoption.model.dto;

/**
 * 宠物信息DTO
 * @author yr
 * @since 2026-01-01
 */
public class PetInfo {

    private Long id;
    private String name;
    private String species;
    private String coverUrl;

    // Getters and Setters
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