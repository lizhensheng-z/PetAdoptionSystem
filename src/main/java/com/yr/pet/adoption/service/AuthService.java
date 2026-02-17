package com.yr.pet.adoption.service;

import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.model.entity.OrgProfileEntity;
import com.yr.pet.adoption.security.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private OrgProfileMapper orgProfileMapper;
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
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRole(user.getRole());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setPermissions(permissions);
        userInfo.setLastLoginTime(user.getLastLoginTime());
        
        // 如果是机构用户，查询机构状态
        if ("ORG".equals(user.getRole())) {
            OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                new LambdaQueryWrapper<OrgProfileEntity>()
                    .eq(OrgProfileEntity::getUserId, user.getId())
                    .eq(OrgProfileEntity::getDeleted, 0)
            );
            
            if (orgProfile != null) {
                userInfo.setOrgStatus(orgProfile.getVerifyStatus());
                userInfo.setOrgProfileComplete(isOrgProfileComplete(orgProfile));
            } else {
                userInfo.setOrgStatus("PENDING");
                userInfo.setOrgProfileComplete(false);
            }
        }
        
        response.setUser(userInfo);
        return response;
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
            
            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(newAccessToken);
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRole(user.getRole());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setPhone(user.getPhone());
            userInfo.setEmail(user.getEmail());
            userInfo.setPermissions(permissions);
            userInfo.setLastLoginTime(user.getLastLoginTime());
            
            // 如果是机构用户，查询机构状态
            if ("ORG".equals(user.getRole())) {
                OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                    new LambdaQueryWrapper<OrgProfileEntity>()
                        .eq(OrgProfileEntity::getUserId, user.getId())
                        .eq(OrgProfileEntity::getDeleted, 0)
                );
                
                if (orgProfile != null) {
                    userInfo.setOrgStatus(orgProfile.getVerifyStatus());
                    userInfo.setOrgProfileComplete(isOrgProfileComplete(orgProfile));
                } else {
                    userInfo.setOrgStatus("PENDING");
                    userInfo.setOrgProfileComplete(false);
                }
            }
            
            response.setUser(userInfo);
            return response;
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
        
        // 如果是机构用户，添加机构状态信息
        if ("ORG".equals(user.getRole())) {
            OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                new LambdaQueryWrapper<OrgProfileEntity>()
                    .eq(OrgProfileEntity::getUserId, user.getId())
                    .eq(OrgProfileEntity::getDeleted, 0)
            );
            
            if (orgProfile != null) {
                response.setOrgStatus(orgProfile.getVerifyStatus());
                response.setOrgProfileComplete(isOrgProfileComplete(orgProfile));
                response.setOrgName(orgProfile.getOrgName());
            } else {
                response.setOrgStatus("PENDING");
                response.setOrgProfileComplete(false);
                response.setOrgName("");
            }
        }
        
        return response;
    }
    
    /**
     * 检查机构资料是否完整
     */
    private boolean isOrgProfileComplete(OrgProfileEntity orgProfile) {
        return orgProfile != null 
                && StringUtils.hasText(orgProfile.getOrgName())
                && StringUtils.hasText(orgProfile.getContactName())
                && StringUtils.hasText(orgProfile.getContactPhone())
                && StringUtils.hasText(orgProfile.getAddress())
                && StringUtils.hasText(orgProfile.getProvince())
                && StringUtils.hasText(orgProfile.getCity());
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