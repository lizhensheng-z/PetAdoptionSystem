package com.yr.pet.adoption.interceptor;

import com.yr.pet.adoption.common.UserContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器
 * 确保在请求结束后清理ThreadLocal中的用户上下文
 */
@Component
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {
    
    private final UserContent userContent;
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理用户上下文
        userContent.clear();
    }
}