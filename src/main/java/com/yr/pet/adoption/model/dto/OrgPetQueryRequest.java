package com.yr.pet.adoption.model.dto;

/**
 * 机构宠物查询请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class OrgPetQueryRequest {

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String status;
    private String sortBy = "createTime";
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