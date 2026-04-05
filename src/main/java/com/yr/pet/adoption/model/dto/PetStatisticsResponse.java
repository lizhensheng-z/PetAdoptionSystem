package com.yr.pet.adoption.model.dto;

/**
 * 宠物统计信息响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class PetStatisticsResponse {

    private Integer viewCount;
    private Integer favoriteCount;
    private Integer applicationCount;
    private Integer adoptedCount;

    // Getters and Setters
    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Integer getAdoptedCount() {
        return adoptedCount;
    }

    public void setAdoptedCount(Integer adoptedCount) {
        this.adoptedCount = adoptedCount;
    }
}