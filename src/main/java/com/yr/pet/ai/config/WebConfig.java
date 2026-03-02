package com.yr.pet.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(taskExecutor());
        configurer.setDefaultTimeout(60_000); // 可根据需要设置超时时间
    }

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);  // 设置核心线程池大小
        executor.setMaxPoolSize(10);  // 设置最大线程池大小
        executor.setQueueCapacity(50); // 设置队列容量
        executor.setThreadNamePrefix("WebFlux-Async-Executor-");
        executor.initialize();
        return executor;  // 返回一个 AsyncTaskExecutor 类型的对象
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")   // 或具体前端 origin
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")        // 关键：允许浏览器带 Authorization
                .allowCredentials(true);
    }
}
