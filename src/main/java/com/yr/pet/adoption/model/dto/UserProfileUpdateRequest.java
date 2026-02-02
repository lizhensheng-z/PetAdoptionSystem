package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户资料更新请求DTO
 */
@Data
@Schema(description = "用户资料更新请求")
public class UserProfileUpdateRequest {
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @Schema(description = "用户偏好设置")
    private UserPreference preference;
    
    /**
     * 用户偏好设置
     */
    @Data
    @Schema(description = "用户偏好设置")
    public static class UserPreference {
        
        @Schema(description = "宠物种类偏好", example = "[\"CAT\", \"DOG\"]")
        private List<String> species;
        
        @Schema(description = "体型偏好", example = "[\"M\", \"L\"]")
        private List<String> sizePreference;
        
        @Schema(description = "年龄范围(月)", example = "[12, 120]")
        private List<Integer> ageRange;
        
        @Schema(description = "性格偏好", example = "[\"亲人\", \"活泼\"]")
        private List<String> personality;
        
        @Schema(description = "健康要求", example = "[\"已绝育\", \"已疫苗\"]")
        private List<String> requiredHealth;
        
        @Schema(description = "城市", example = "北京")
        private String city;
    }
}