package com.yr.pet.adoption.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 权限创建/更新请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class PermissionRequest {

    @NotBlank(message = "权限编码不能为空")
    private String permCode;

    @NotBlank(message = "权限名称不能为空")
    private String permName;

    @NotBlank(message = "权限类型不能为空")
    private String permType;

    private String httpMethod;

    private String apiPath;

    private Long parentId;

    private Integer sort;

    private Byte enabled;

    private String remark;

    public String getPermCode() {
        return permCode;
    }

    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public String getPermType() {
        return permType;
    }

    public void setPermType(String permType) {
        this.permType = permType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}