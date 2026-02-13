package com.yr.pet.adoption.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.common.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT权限拒绝处理器
 * 处理无权限请求
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        R<Void> result = R.fail(ErrorCode.FORBIDDEN, "无权限访问");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}