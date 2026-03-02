package com.yr.pet.ai.model.enums;

import org.springframework.http.HttpStatus;

/**
 * DeepSeek API错误码枚举
 */
public enum DeepSeekErrorCode {

    // 客户端错误
    BAD_REQUEST(400, "格式错误", "请求体格式错误", "请根据错误信息提示修改请求体"),
    UNAUTHORIZED(401, "认证失败", "API key错误，认证失败", "请检查您的API key是否正确，如没有API key，请先创建API key"),
    PAYMENT_REQUIRED(402, "余额不足", "账号余额不足", "请确认账户余额，并前往充值页面进行充值"),
    UNPROCESSABLE_ENTITY(422, "参数错误", "请求体参数错误", "请根据错误信息提示修改相关参数"),
    TOO_MANY_REQUESTS(429, "请求速率达到上限", "请求速率（TPM或RPM）达到上限", "请合理规划您的请求速率"),

    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器故障", "服务器内部故障", "请等待后重试。若问题一直存在，请联系我们解决"),
    SERVICE_UNAVAILABLE(503, "服务器繁忙", "服务器负载过高", "请稍后重试您的请求");

    /**
     * HTTP状态码（如401、500）
     */
    private final int statusCode;

    /**
     * 错误简短描述（如“认证失败”）
     */
    private final String description;

    /**
     * 错误原因
     */
    private final String reason;

    /**
     * 解决方法
     */
    private final String solution;

    DeepSeekErrorCode(int statusCode, String description, String reason, String solution) {
        this.statusCode = statusCode;
        this.description = description;
        this.reason = reason;
        this.solution = solution;
    }

    // 核心方法：根据HTTP状态码获取对应的枚举实例
    public static DeepSeekErrorCode fromStatusCode(int statusCode) {
        for (DeepSeekErrorCode errorCode : values()) {
            if (errorCode.statusCode == statusCode) {
                return errorCode;
            }
        }
        // 未匹配到已知错误码时，返回默认服务器错误
        return INTERNAL_SERVER_ERROR;
    }

    // getter方法
    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public String getReason() {
        return reason;
    }

    public String getSolution() {
        return solution;
    }
    /**
     * 生成前端友好的错误提示常量
     */
    public static final String ERROR_TO_USER= "不好意思，刚刚和小律聊的人太多了。小律有点累了，可以再问我一遍。";
    /**
     * 生成前端友好的错误提示（简洁版）
     */
    public String getClientMsg() {
        return String.format("[ERROR] %s：%s", description, solution);
    }

    /**
     * 生成详细错误信息（用于日志/落库）
     */
    public String getDetailMsg() {
        return String.format("状态码：%d，错误：%s，原因：%s，解决方法：%s",
                statusCode, description, reason, solution);
    }
}