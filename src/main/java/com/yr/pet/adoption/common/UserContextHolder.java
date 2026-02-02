package com.yr.pet.adoption.common;

import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户上下文持有者工具类
 * 提供静态方法访问当前用户信息
 */
@Component
@RequiredArgsConstructor
public class UserContextHolder {
    
    private static UserContent userContent;
    
    public UserContextHolder(UserContent userContent) {
        UserContextHolder.userContent = userContent;
    }
    
    /**
     * 获取当前用户上下文
     */
    public static UserContext getCurrentUser() {
        return userContent.getUserContext();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return userContent.getUserId();
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return userContent.getUsername();
    }
    
    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        return userContent.getRole();
    }
    
    /**
     * 判断当前用户是否登录
     */
    public static boolean isLogin() {
        return userContent.isLogin();
    }
    
    /**
     * 判断当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        return userContent.hasRole(role);
    }
    
    /**
     * 判断当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        return userContent.hasPermission(permission);
    }
}