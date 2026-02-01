package com.yr.pet.adoption.common;

/**
 * 错误码枚举
 * 分段规则：
 * 10000 通用
 * 20000 参数校验
 * 30000 鉴权/认证
 * 40000 业务错误
 * 50000 系统错误
 */
public enum ErrorCode {
    
    // 通用 10000
    SUCCESS(10000, "操作成功"),
    FAILED(10001, "操作失败"),
    
    // 参数校验 20000
    PARAM_ERROR(20001, "参数错误"),
    PARAM_MISSING(20002, "缺少必要参数"),
    PARAM_TYPE_ERROR(20003, "参数类型错误"),
    PARAM_VALID_ERROR(20004, "参数校验失败"),
    
    // 鉴权/认证 30000
    UNAUTHORIZED(30001, "未登录"),
    TOKEN_EXPIRED(30002, "token已过期"),
    TOKEN_INVALID(30003, "token无效"),
    FORBIDDEN(30004, "无权限访问"),
    LOGIN_FAILED(30005, "用户名或密码错误"),
    
    // 业务错误 40000
    RESOURCE_NOT_FOUND(40001, "资源不存在"),
    RESOURCE_EXIST(40002, "资源已存在"),
    OPERATION_NOT_ALLOWED(40003, "操作不允许"),
    DUPLICATE_OPERATION(40004, "重复操作"),
    
    // 系统错误 50000
    SYSTEM_ERROR(50000, "系统异常"),
    DATABASE_ERROR(50001, "数据库异常"),
    NETWORK_ERROR(50002, "网络异常"),
    SERVICE_UNAVAILABLE(50003, "服务暂不可用");
    
    private final Integer code;
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}