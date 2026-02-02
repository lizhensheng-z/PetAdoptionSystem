package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "用户角色")
    private String role;
    
    @Schema(description = "用户权限列表")
    private List<String> permissions;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    public LoginResponse(String accessToken, Long userId, String username, String role, List<String> permissions, String avatar) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
        this.avatar = avatar;
    }
}