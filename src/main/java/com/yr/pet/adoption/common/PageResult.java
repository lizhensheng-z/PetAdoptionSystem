package com.yr.pet.adoption.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    
    private List<T> list;
    private Integer pageNo;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;
    
    public PageResult() {}
    
    public PageResult(List<T> list, Integer pageNo, Integer pageSize, Long total, Integer totalPages) {
        this.list = list;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
    }
    
    public static <T> PageResult<T> of(List<T> list, Integer pageNo, Integer pageSize, Long total) {
        Integer totalPages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(list, pageNo, pageSize, total, totalPages);
    }
    
    public static <T> PageResult<T> of(Long total, Long size, Long current, List<T> records) {
        Integer pageNo = current.intValue();
        Integer pageSize = size.intValue();
        Integer totalPages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(records, pageNo, pageSize, total, totalPages);
    }
}