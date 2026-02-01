package com.yr.pet.adoption.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    
    private Long total;
    private Long size;
    private Long current;
    private List<T> records;
    
    public PageResult() {}
    
    public PageResult(Long total, Long size, Long current, List<T> records) {
        this.total = total;
        this.size = size;
        this.current = current;
        this.records = records;
    }
    
    public static <T> PageResult<T> of(Long total, Long size, Long current, List<T> records) {
        return new PageResult<>(total, size, current, records);
    }
}