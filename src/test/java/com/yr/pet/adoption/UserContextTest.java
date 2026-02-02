package com.yr.pet.adoption;

import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.common.UserContextHolder;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserContextTest {

    @Autowired
    private UserContent userContent;
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testUserContextStorage() {
        // 模拟设置用户上下文
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole("USER");
        user.setEmail("test@example.com");
        
        userContent.setUser(user, Collections.singletonList("ROLE_USER"));
        
        // 测试用户上下文
        assertEquals(1L, userContent.getUserId());
        assertEquals("testuser", userContent.getUsername());
        assertEquals("USER", userContent.getRole());
        assertTrue(userContent.hasRole("USER"));
        assertTrue(userContent.hasPermission("ROLE_USER"));
        
        // 测试静态工具类
        assertEquals(1L, UserContextHolder.getCurrentUserId());
        assertEquals("testuser", UserContextHolder.getCurrentUsername());
        assertTrue(UserContextHolder.isLogin());
        
        // 清理上下文
        userContent.clear();
        assertNull(userContent.getUserContext());
        assertFalse(userContent.isLogin());
    }
    
    @Test
    public void testSecurityContextIntegration() {
        // 模拟Spring Security上下文
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 测试从SecurityContext初始化
        userContent.initUserContextFromSecurity();
        
        // 验证结果
        assertNotNull(userContent.getUsername());
        
        // 清理
        SecurityContextHolder.clearContext();
        userContent.clear();
    }
}