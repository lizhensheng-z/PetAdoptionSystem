package com.yr.pet.adoption;

import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.model.dto.LoginRequest;
import com.yr.pet.adoption.model.dto.RegisterRequest;
import com.yr.pet.adoption.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class AuthTest {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void testPasswordEncoder() {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        System.out.println("密码匹配: " + passwordEncoder.matches(rawPassword, encodedPassword));
    }
    
    @Test
    public void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser" + System.currentTimeMillis());
        registerRequest.setPassword("123456");
        registerRequest.setPhone("13800138000");
        registerRequest.setEmail("test@example.com");
        registerRequest.setRole("USER");
        
        try {
            var user = authService.register(registerRequest);
            System.out.println("注册成功: " + user.getUsername());
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("123456");
        
        try {
            var response = authService.login(loginRequest);
            System.out.println("登录成功: " + response.getAccessToken());
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
        }
    }
}