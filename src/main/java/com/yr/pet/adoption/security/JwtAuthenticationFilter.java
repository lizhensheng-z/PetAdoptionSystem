package com.yr.pet.adoption.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContent;
import com.yr.pet.adoption.model.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 集成到Spring Security过滤器链中，确保认证信息在Spring Security处理请求前设置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final DatabaseUserDetailsService userDetailsService;
    private final UserContent userContent;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());



    @Override
    public void destroy() {
        super.destroy();
        userContent.clear();
        SecurityContextHolder.clearContext();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String requestUri = request.getRequestURI();
            log.debug("JWT Filter processing request: {}", requestUri);
            log.debug("Authorization header: {}", request.getHeader(HttpHeaders.AUTHORIZATION));

            // 跳过不需要认证的路径
            if (shouldSkipAuthentication(request)) {
                log.debug("Skipping authentication for: {}", requestUri);
                filterChain.doFilter(request, response);
                return;
            }
            
            log.debug("Processing authentication for: {}", requestUri);

            String token = getTokenFromRequest(request);

            // 需要认证但没有token
            if (!StringUtils.hasText(token)) {
                log.warn("请求 {} 缺少 JWT token", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);

            if (username == null) {
                log.warn("无效的token");
                filterChain.doFilter(request, response);
                return;
            }

            // 检查SecurityContext中是否已有认证信息
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("SecurityContext中已有认证信息");
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(token, userDetails)) {
                log.warn("token已过期或无效");
                filterChain.doFilter(request, response);
                return;
            }

            // 认证成功，设置SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 设置用户上下文信息到ThreadLocal
            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                List<String> permissions = userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toList());
                
                // 创建UserEntity用于ThreadLocal设置
                UserEntity user = new UserEntity();
                user.setId(customUserDetails.getUserId());
                user.setUsername(customUserDetails.getUsername());
                user.setRole(customUserDetails.getRole());
                
                userContent.setUser(user, permissions);

                log.debug("用户 {} 认证成功，角色: {}", username, customUserDetails.getRole());
            }

        } catch (Exception e) {
            log.error("JWT token 验证失败: {}", e.getMessage());
            // 不中断过滤器链，让Spring Security处理未认证的情况
        }
    }

    /**
     * 判断是否需要跳过认证
     */
    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        // 公开接口 - 只开放特定的认证相关接口
        if (requestUri.equals("/api/auth/login") ||
            requestUri.equals("/api/auth/register") ||
            requestUri.equals("/api/auth/refresh-token") ||
            requestUri.equals("/api/auth/logout") ||
            requestUri.startsWith("/swagger-ui/") ||
            requestUri.startsWith("/v3/api-docs/") ||
            requestUri.equals("/swagger-ui.html") ||
            requestUri.startsWith("/actuator/") ||
            requestUri.startsWith("/error")) {
            return true;
        }

        // GET 请求获取宠物列表/详情不需要认证
        if ("GET".equals(method)) {
            if (requestUri.equals("/api/pets") ||
                    requestUri.startsWith("/api/pets/")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从请求头中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}