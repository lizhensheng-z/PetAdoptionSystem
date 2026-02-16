package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;



/**
 * 用户列表查询请求
 */
@Data
@Schema(description = "用户列表查询请求")
public class UserListRequest {

    @Schema(description = "页码，从1开始", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer pageSize = 10;

    @Schema(description = "搜索关键词（用户名/手机号/邮箱）", example = "张三")
    private String keyword;

    @Schema(description = "角色筛选：USER/ORG/ADMIN", example = "USER")
    private String role;

    @Schema(description = "状态筛选：NORMAL/BANNED", example = "NORMAL")
    private String status;

    @Schema(description = "创建开始时间（YYYY-MM-DD）", example = "2024-01-01")
    private String startDate;

    @Schema(description = "创建结束时间（YYYY-MM-DD）", example = "2024-12-31")
    private String endDate;

    @Schema(description = "排序字段", example = "createTime")
    private String sortBy = "createTime";

    @Schema(description = "排序方式：asc/desc", example = "desc")
    private String order = "desc";
}