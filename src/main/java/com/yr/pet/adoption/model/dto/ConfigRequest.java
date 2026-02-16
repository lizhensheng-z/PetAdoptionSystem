package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 系统配置请求DTO
 * @author 榕
 * @since 2026-02-01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ConfigRequest", description = "系统配置请求")
public class ConfigRequest {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 64, message = "配置键长度不能超过64个字符")
    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    /**
     * 配置值（字符串/JSON）
     */
    @NotBlank(message = "配置值不能为空")
    @Size(max = 2000, message = "配置值长度不能超过2000个字符")
    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注")
    private String remark;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ConfigRequest{" +
                "configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}