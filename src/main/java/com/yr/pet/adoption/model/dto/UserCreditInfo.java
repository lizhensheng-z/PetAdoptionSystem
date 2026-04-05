package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 用户信用信息DTO
 * @author yr
 * @since 2026-01-01
 */
public class UserCreditInfo {
    
    private Integer score;
    private Integer level;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}