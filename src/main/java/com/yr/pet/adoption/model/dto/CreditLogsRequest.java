package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 信用日志查询请求DTO
 * 用于查询用户信用积分的变更记录
 * 
 * @author 宗平
 * @since 2026-02-18
 */
@Data
@Schema(description = "信用日志查询请求")
public class CreditLogsRequest {

    @Schema(description = "页码，从1开始", example = "1", defaultValue = "1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数，最大50", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "筛选特定类型的记录", example = "CHECKIN")
    private String type;

    @Schema(description = "开始日期，格式：YYYY-MM-DD", example = "2026-02-01")
    private String startDate;

    @Schema(description = "结束日期，格式：YYYY-MM-DD", example = "2026-02-18")
    private String endDate;
}