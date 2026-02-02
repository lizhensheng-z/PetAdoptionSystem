package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.common.UserContextHolder;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户上下文服务示例
 * 演示如何在服务层使用用户上下文
 */
@Service
@RequiredArgsConstructor
public class UserContextService {
    
    private final UserService userService;
    private final UserContent userContent;
    
    /**
     * 获取当前用户的详细信息
     */
    public UserEntity getCurrentUserInfo() {
        Long userId = userContent.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userService.getById(userId);
    }
    
    /**
     * 检查当前用户是否有权限访问某个资源
     */
    public boolean hasAccessToResource(Long resourceOwnerId) {
        // 管理员可以访问所有资源
        if (userContent.hasRole("ADMIN")) {
            return true;
        }
        
        // 用户只能访问自己的资源
        Long currentUserId = userContent.getUserId();
        return currentUserId != null && currentUserId.equals(resourceOwnerId);
    }
    
    /**
     * 获取当前用户的操作日志前缀
     */
    public String getOperationLogPrefix() {
        String username = userContent.getUsername();
        Long userId = userContent.getUserId();
        return String.format("[用户: %s (ID: %d)]", username, userId);
    }
    
    /**
     * 使用静态工具类获取用户信息
     */
    public void demonstrateStaticUsage() {
        if (UserContextHolder.isLogin()) {
            System.out.println("当前用户ID: " + UserContextHolder.getCurrentUserId());
            System.out.println("当前用户名: " + UserContextHolder.getCurrentUsername());
            System.out.println("当前用户角色: " + UserContextHolder.getCurrentUserRole());
        }
    }
}