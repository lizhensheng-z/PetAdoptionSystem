package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 收藏列表项DTO
 * @author yr
 * @since 2026-01-01
 */
public class FavoriteListItem {

    private Long id;
    private PetSimpleInfo pet;
    private LocalDateTime favoritedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PetSimpleInfo getPet() {
        return pet;
    }

    public void setPet(PetSimpleInfo pet) {
        this.pet = pet;
    }

    public LocalDateTime getFavoritedTime() {
        return favoritedTime;
    }

    public void setFavoritedTime(LocalDateTime favoritedTime) {
        this.favoritedTime = favoritedTime;
    }

    public static class PetSimpleInfo {
        private Long id;
        private String name;
        private String species;
        private String coverUrl;
        private String orgName;
        private String status;

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

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}