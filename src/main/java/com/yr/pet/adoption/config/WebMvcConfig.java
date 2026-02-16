package com.yr.pet.adoption.config;

import com.yr.pet.adoption.interceptor.UserContextInterceptor;
import com.yr.pet.adoption.security.JwtAuthenticationInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
    
    @Resource
    private UserContextInterceptor userContextInterceptor;

@Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册认证拦截器 - 设置用户上下文
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API路径
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register", 
                        "/api/auth/refresh-token",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/error"
                );
        
        // 最后注册清理拦截器 - 确保在所有处理完成后清理上下文
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/api/**");
    }
    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "https://*.petadoption.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
