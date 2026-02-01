package com.yr.pet.adoption.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义用户详情服务（内存实现）
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final Map<String, CustomUserDetails> userDetailsMap = new ConcurrentHashMap<>();
    
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        // 初始化内存用户
        userDetailsMap.put("admin", new CustomUserDetails(
                "admin",
                passwordEncoder.encode("123456"),
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("admin:hello")
                )
        ));
        
        userDetailsMap.put("user", new CustomUserDetails(
                "user",
                passwordEncoder.encode("123456"),
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        ));
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userDetails = userDetailsMap.get(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return userDetails;
    }
    
    public List<CustomUserDetails> getAllUsers() {
        return List.copyOf(userDetailsMap.values());
    }
}