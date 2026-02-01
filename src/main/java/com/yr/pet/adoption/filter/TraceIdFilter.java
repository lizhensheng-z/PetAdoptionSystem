package com.yr.pet.adoption.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 请求链路追踪过滤器
 * 生成并传递traceId，记录请求日志
 */
@Slf4j
@Component
public class TraceIdFilter implements Filter {
    
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取或生成traceId
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        
        // 设置到MDC
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        
        // 设置响应头
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("[{}] 开始请求: {} {}", traceId, httpRequest.getMethod(), httpRequest.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] 结束请求: {} {} - 耗时: {}ms - 状态码: {}", 
                    traceId, 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(),
                    duration,
                    httpResponse.getStatus());
            
            // 清理MDC
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}