package com.yr.pet.adoption.security;

import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证拦截器
 * 替代原有的JWT认证过滤器，更贴近业务层
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final DatabaseUserDetailsService userDetailsService;
    private final UserService userService;
    private final UserContent userContent;
    
    /**
     * 在请求处理之前进行JWT认证
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 清除之前的用户上下文
        userContent.clear();
        
        // 跳过不需要认证的路径
        if (shouldSkipAuthentication(request)) {
            return true;
        }
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, 
                                        null, 
                                        jwtUtil.getAuthoritiesFromToken(token)
                                );
                        authentication.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // 设置用户上下文信息
                        UserEntity user = userService.findByUsername(username);
                        if (user != null) {
                            List<String> permissions = userDetails.getAuthorities().stream()
                                    .map(auth -> auth.getAuthority())
                                    .collect(Collectors.toList());
                            userContent.setUser(user, permissions);
                            
                            log.debug("用户 {} 认证成功，角色: {}", username, user.getRole());
                        }
                    } else {
                        log.warn("JWT token验证失败: {}", request.getRequestURI());
                    }
                }
            } catch (Exception e) {
                log.error("JWT token验证失败: {}", e.getMessage());
                // 不抛出异常，让请求继续处理，后续的安全检查会处理未认证的情况
            }
        } else {
            // 没有token的情况，清除SecurityContext
            SecurityContextHolder.clearContext();
        }
        
        return true;
    }
    
    /**
     * 请求处理完成后清理用户上下文
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清除用户上下文
        userContent.clear();
        
        // 可选：清除SecurityContext，避免线程复用问题
        // SecurityContextHolder.clearContext();
    }
    
    /**
     * 判断是否需要跳过认证
     */
    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        // 跳过公开接口
        return requestUri.startsWith("/api/auth/") ||
               requestUri.startsWith("/swagger-ui/") ||
               requestUri.startsWith("/v3/api-docs/") ||
               requestUri.equals("/swagger-ui.html") ||
               requestUri.startsWith("/actuator/") ||
               (requestUri.equals("/api/pets") && "GET".equals(method)) ||  // 获取宠物列表不需要认证
               (requestUri.startsWith("/api/pets/") && "GET".equals(method)); // 获取单个宠物详情不需要认证
    }
    
    /**
     * 从请求头中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}