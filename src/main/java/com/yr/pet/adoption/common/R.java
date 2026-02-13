package com.yr.pet.adoption.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yr.pet.adoption.exception.ErrorCode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一返回结果
 * @param <T> 数据类型
 */
@Data
public class R<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private R() {
        this.timestamp = LocalDateTime.now();
    }
    
    public static <T> R<T> ok() {
        return ok(null);
    }
    
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ErrorCode.SUCCESS.getCode());
        r.setMessage(ErrorCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }
    
    public static <T> R<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }
    
    public static <T> R<T> fail(ErrorCode errorCode, String message) {
        return fail(errorCode.getCode(), message);
    }
    
    public static <T> R<T> fail(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
    
    public static <T> R<T> fail(String message) {
        return fail(ErrorCode.SYSTEM_ERROR.getCode(), message);
    }
}