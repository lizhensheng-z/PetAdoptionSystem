package com.yr.pet.adoption.service;

import com.yr.pet.adoption.model.dto.LoginRequest;
import com.yr.pet.adoption.model.dto.LoginResponse;
import com.yr.pet.adoption.model.dto.RegisterRequest;
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
        
        UserEntity user = userService.findByUsername(loginRequest.getUsername());
        
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
        
        return new LoginResponse(
                token,
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
    public LoginResponse refreshToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
        
        UserEntity user = userService.findByUsername(username);
        
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
        
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole(),
                permissions,
                user.getAvatar()
        );
    }
}