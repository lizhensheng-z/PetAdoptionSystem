package com.yr.pet.adoption.common;

import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户上下文工具类
 * 使用ThreadLocal存储当前登录用户的信息
 */
@Component
@RequiredArgsConstructor
public class UserContent {
    
    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();
    
    private final UserService userService;
    
    /**
     * 设置当前用户上下文
     */
    public void setUserContext(UserContext userContext) {
        USER_CONTEXT.set(userContext);
    }
    
    /**
     * 获取当前用户上下文
     */
    public UserContext getUserContext() {
        return USER_CONTEXT.get();
    }
    
    /**
     * 获取当前用户ID
     */
    public Long getUserId() {
        UserContext context = getUserContext();
        return context != null ? context.getUserId() : null;
    }
    
    /**
     * 获取当前用户名
     */
    public String getUsername() {
        UserContext context = getUserContext();
        return context != null ? context.getUsername() : null;
    }
    
    /**
     * 获取当前用户角色
     */
    public String getRole() {
        UserContext context = getUserContext();
        return context != null ? context.getRole() : null;
    }
    
    /**
     * 获取当前用户权限列表
     */
    public List<String> getPermissions() {
        UserContext context = getUserContext();
        return context != null ? context.getPermissions() : null;
    }
    
    /**
     * 判断当前用户是否登录
     */
    public boolean isLogin() {
        return getUserContext() != null;
    }
    
    /**
     * 判断当前用户是否有指定角色
     */
    public boolean hasRole(String role) {
        UserContext context = getUserContext();
        return context != null && role.equals(context.getRole());
    }
    
    /**
     * 判断当前用户是否有指定权限
     */
    public boolean hasPermission(String permission) {
        UserContext context = getUserContext();
        return context != null && 
               context.getPermissions() != null && 
               context.getPermissions().contains(permission);
    }
    
    /**
     * 从Spring Security上下文中初始化用户信息
     */
    public void initUserContextFromSecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = authentication.getName();
            UserEntity user = userService.findByUsername(username);
            if (user != null) {
                UserContext userContext = new UserContext();
                userContext.setUserId(user.getId());
                userContext.setUsername(user.getUsername());
                userContext.setRole(user.getRole());
                userContext.setAvatar(user.getAvatar());
                userContext.setPhone(user.getPhone());
                userContext.setEmail(user.getEmail());
                
                List<String> permissions = authentication.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList());
                userContext.setPermissions(permissions);
                
                setUserContext(userContext);
            }
        }
    }
    
    /**
     * 清除当前用户上下文
     */
    public void clear() {
        USER_CONTEXT.remove();
    }
    
    /**
     * 设置用户信息到上下文
     */
    public void setUser(UserEntity user, List<String> permissions) {
        UserContext userContext = new UserContext();
        userContext.setUserId(user.getId());
        userContext.setUsername(user.getUsername());
        userContext.setRole(user.getRole());
        userContext.setAvatar(user.getAvatar());
        userContext.setPhone(user.getPhone());
        userContext.setEmail(user.getEmail());
        userContext.setPermissions(permissions);
        setUserContext(userContext);
    }
}