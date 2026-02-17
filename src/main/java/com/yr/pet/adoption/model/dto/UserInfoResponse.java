package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息响应DTO
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "用户角色")
    private String role;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "用户状态")
    private String status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "权限列表")
    private List<String> permissions;
    
    @Schema(description = "机构名称")
    private String orgName;
    
    @Schema(description = "机构认证状态")
    private String orgStatus;
    
    @Schema(description = "机构资料是否完整")
    private Boolean orgProfileComplete;
}