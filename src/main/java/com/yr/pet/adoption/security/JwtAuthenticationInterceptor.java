package com.yr.pet.adoption.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yr.pet.adoption.common.R;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证拦截器
 * 统一处理认证和授权
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final DatabaseUserDetailsService userDetailsService;
    private final UserService userService;
    private final UserContent userContent;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 清除之前的用户上下文
        userContent.clear();
        SecurityContextHolder.clearContext();

        // 跳过不需要认证的路径
        if (shouldSkipAuthentication(request)) {
            return true;
        }

        String token = getTokenFromRequest(request);

        // 需要认证但没有 token
        if (!StringUtils.hasText(token)) {
            log.warn("请求 {} 缺少 JWT token", request.getRequestURI());
            writeUnauthorizedResponse(response, "请先登录");
            return false;
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);

            if (username == null) {
                writeUnauthorizedResponse(response, "无效的token");
                return false;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(token, userDetails)) {
                writeUnauthorizedResponse(response, "token已过期或无效");
                return false;
            }

            // 认证成功，设置 SecurityContext
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

            return true;

        } catch (Exception e) {
            log.error("JWT token 验证失败: {}", e.getMessage());
            writeUnauthorizedResponse(response, "token验证失败: " + e.getMessage());
            return false;
        }
    }

@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 注意：不在这里清理用户上下文，由专门清理上下文的拦截器处理
        // 避免在请求处理过程中上下文被过早清理
    }

/**
     * 判断是否需要跳过认证
     */
    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        // 公开接口 - 只排除特定的认证相关接口
        if (requestUri.equals("/api/auth/login") ||
            requestUri.equals("/api/auth/register") ||
            requestUri.equals("/api/auth/refresh-token") ||
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
                    (requestUri.startsWith("/api/pets/") && !requestUri.startsWith("/api/pets/org/"))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从请求头中获取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 写入 401 响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        R<?> result = R.fail(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}