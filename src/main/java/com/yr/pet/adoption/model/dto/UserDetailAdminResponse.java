package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户详情响应（管理员专用）
 */
@Data
@Schema(description = "用户详情响应（管理员专用）")
public class UserDetailAdminResponse {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色：USER/ORG/ADMIN")
    private String role;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "状态：NORMAL/BANNED")
    private String status;

    @Schema(description = "用户偏好JSON")
    private String preferenceJson;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "角色显示名称")
    public String getRoleName() {
        if (role == null) return "";
        switch (role) {
            case "USER":
                return "普通用户";
            case "ORG":
                return "机构用户";
            case "ADMIN":
                return "管理员";
            default:
                return role;
        }
    }

    @Schema(description = "状态显示名称")
    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case "NORMAL":
                return "正常";
            case "BANNED":
                return "已禁用";
            default:
                return status;
        }
    }
}