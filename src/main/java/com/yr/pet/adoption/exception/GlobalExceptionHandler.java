package com.yr.pet.adoption.exception;

import com.yr.pet.adoption.common.BizException;
import com.yr.pet.adoption.common.ErrorCode;
import com.yr.pet.adoption.common.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(e.getErrorCode());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logError(e, request);
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return R.fail(ErrorCode.PARAM_VALID_ERROR, message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        logError(e, request);
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return R.fail(ErrorCode.PARAM_VALID_ERROR, message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(ErrorCode.PARAM_TYPE_ERROR, "参数类型错误: " + e.getName());
    }

    /**
     * 处理请求体读取异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(ErrorCode.PARAM_ERROR, "请求体格式错误");
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(ErrorCode.UNAUTHORIZED, "未登录或认证失败");
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(ErrorCode.FORBIDDEN, "无权限访问");
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        logError(e, request);
        return R.fail(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 记录错误日志
     */
    private void logError(Exception e, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        log.error("[{}] 请求异常: {} {}, 错误: {}", 
                traceId,
                request.getMethod(), 
                request.getRequestURI(),
                e.getMessage(), 
                e);
    }
}