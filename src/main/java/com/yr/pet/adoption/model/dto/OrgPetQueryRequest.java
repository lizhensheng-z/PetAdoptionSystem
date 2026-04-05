package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 机构宠物查询请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class OrgPetQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "关键词搜索(宠物名称、品种)", example = "大黄")
    private String keyword;
    
    @Schema(description = "状态筛选", example = "PUBLISHED")
    private String status;
    
    @Schema(description = "排序字段", example = "createTime")
    private String sortBy = "createTime";
    
    @Schema(description = "排序方式", example = "desc")
    private String order = "desc";

    // Getters and Setters
    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}