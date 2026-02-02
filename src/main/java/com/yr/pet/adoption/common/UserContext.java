package com.yr.pet.adoption.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户上下文信息实体类
 * 存储当前登录用户的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 用户权限列表
     */
    private List<String> permissions;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
}