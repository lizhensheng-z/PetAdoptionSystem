package com.yr.pet.adoption.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果包装类
 * @param <T> 数据类型
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo;

    @Schema(description = "每页数量", example = "12")
    private Integer pageSize;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "9")
    private Integer totalPages;



    public PageResult(List<T> list, Integer pageNo, Integer pageSize, Long total, Integer totalPages) {
        this.list = list;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
    }

    public PageResult(List<T> list, Integer pageNo, Integer pageSize, Long total) {
        this.list = list;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public PageResult() {

    }
}