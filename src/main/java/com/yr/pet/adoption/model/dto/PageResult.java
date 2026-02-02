package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> list;
    
    @Schema(description = "页码")
    private Integer pageNo;
    
    @Schema(description = "每页数量")
    private Integer pageSize;
    
    @Schema(description = "总数")
    private Long total;
    
    @Schema(description = "总页数")
    private Integer totalPages;
}