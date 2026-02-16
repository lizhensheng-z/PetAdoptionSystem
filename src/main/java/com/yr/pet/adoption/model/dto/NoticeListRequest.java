package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 公告列表查询请求DTO
 */
@Data
@Schema(description = "公告列表查询请求")
public class NoticeListRequest {
    
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "公告标题模糊搜索", example = "维护")
    private String title;
    
    @Schema(description = "公告状态筛选：DRAFT/PUBLISHED/REMOVED", example = "PUBLISHED")
    private String status;
}