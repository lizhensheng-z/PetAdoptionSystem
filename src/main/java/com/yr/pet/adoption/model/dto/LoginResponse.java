package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录响应DTO
 * @author 榕
 * @since 2026-02-17
 */
@Schema(name = "LoginResponse", description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * 用户信息内部类
     */
    @Schema(name = "UserInfo", description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "角色")
        private String role;

        @Schema(description = "机构认证状态")
        private String orgStatus;

        @Schema(description = "机构资料是否完整")
        private Boolean orgProfileComplete;

        @Schema(description = "头像URL")
        private String avatar;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "权限列表")
        private List<String> permissions;

        @Schema(description = "最后登录时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastLoginTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getOrgStatus() {
            return orgStatus;
        }

        public void setOrgStatus(String orgStatus) {
            this.orgStatus = orgStatus;
        }

        public Boolean getOrgProfileComplete() {
            return orgProfileComplete;
        }

        public void setOrgProfileComplete(Boolean orgProfileComplete) {
            this.orgProfileComplete = orgProfileComplete;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

        public LocalDateTime getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(LocalDateTime lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }
    }
}