package com.yr.pet.adoption.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import com.yr.pet.adoption.security.CustomUserDetails;

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
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return null;
    }
    
    /**
     * 获取当前登录用户上下文
     */
    public static UserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserContext context = new UserContext();
            context.setUserId(userDetails.getUserId());
            context.setUsername(userDetails.getUsername());
            context.setRole(userDetails.getRole());
            context.setPermissions(userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList()));
            return context;
        }
        return null;
    }
}