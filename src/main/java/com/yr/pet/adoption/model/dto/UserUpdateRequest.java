package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;



/**
 * 用户信息更新请求
 */
@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateRequest {

    @Schema(description = "手机号", example = "13800000001")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "邮箱", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "角色：USER/ORG/ADMIN", example = "USER")
    private String role;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "状态：NORMAL/BANNED", example = "NORMAL")
    private String status;
}