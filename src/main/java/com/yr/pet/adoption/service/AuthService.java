package com.yr.pet.adoption.service;

import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.security.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Resource
    private  AuthenticationManager authenticationManager;
    @Resource
    private  UserDetailsService userDetailsService;
    @Resource
    private  JwtUtil jwtUtil;
    @Resource
    private  UserService userService;
    @Resource
    private  UserContent userContent;
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        UserEntity user = userService.findByUsername(loginRequest.getUsername());
        
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
        
        // 更新最后登录时间
        userService.updateLastLoginTime(user.getId());
        
        return new LoginResponse(
                token,
                refreshToken,
                3600, // 1小时有效期
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getRole(),
                permissions,
                user.getAvatar()
        );
    }
    
    /**
     * 用户注册
     */
    public UserEntity register(RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }
    
    /**
     * 刷新令牌
     */
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            
            UserEntity user = userService.findByUsername(username);
            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList();
            
            return new LoginResponse(
                    newAccessToken,
                    refreshToken,
                    3600,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    permissions,
                    user.getAvatar()
            );
        }
        
        throw new RuntimeException("无效的刷新令牌");
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        // 将当前token加入黑名单（Redis实现）
        String username = userContent.getUsername();
        if (username != null) {
            userService.addTokenToBlacklist(username);
        }
        SecurityContextHolder.clearContext();
    }
    
    /**
     * 获取当前用户信息
     */
    public UserInfoResponse getCurrentUserInfo() {
        Long userId = userContent.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        UserEntity user = userService.findById(userId);
        List<String> permissions = userContent.getPermissions();
        
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        response.setPermissions(permissions);
        
        return response;
    }
    
    /**
     * 修改个人资料
     */
    public void updateProfile(UserProfileUpdateRequest request) {
        Long userId = userContent.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        userService.updateUserProfile(userId, request);
    }
}