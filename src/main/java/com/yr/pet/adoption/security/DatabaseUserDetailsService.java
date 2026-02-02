package com.yr.pet.adoption.security;

import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 数据库用户详情服务
 * 从数据库加载用户信息
 */
@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {
    
    private final UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.lambdaQuery()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDeleted, 0)
                .one();
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if ("BANNED".equals(user.getStatus())) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        // 根据用户角色设置权限
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
        
        return new CustomUserDetails(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}