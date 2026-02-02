package com.yr.pet.adoption.exception;

import com.yr.pet.adoption.common.ErrorCode;
import com.yr.pet.adoption.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return R.fail(ErrorCode.PARAM_VALID_ERROR, "参数校验失败");
    }

    /**
     * 处理用户名不存在异常
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public R<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return R.fail(ErrorCode.LOGIN_FAILED, e.getMessage());
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public R<Void> handleBadCredentialsException(BadCredentialsException e) {
        return R.fail(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return R.fail(e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return R.fail(ErrorCode.SYSTEM_ERROR, "系统内部错误");
    }
}