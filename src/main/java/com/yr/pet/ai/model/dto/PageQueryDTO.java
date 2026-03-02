package com.yr.pet.ai.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 分页查询- 分页条目
 *
 * @author MFeng
 * @since 2021-08-12
 */
@Data
public class PageQueryDTO<T> {

    Integer pageSize = 10;

    Integer pageNum = 1;

//    @ApiModelProperty(value = "查询关键字")
    String keyWord;

//    @ApiModelProperty(value = "排序字段")
    String sortKey;

//    @ApiModelProperty(value = "排序类型")
    String sortType;

    public Page<T> getPage(){
       return Page.of(this.pageNum,this.pageSize);
    }
}
