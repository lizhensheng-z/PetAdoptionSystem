package com.yr.pet.ai.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

import java.util.Date;

/**
 *
 *
 * @author QinFQ
 * @since 2023/8/23
 */
@Data
public class BaseModel {

    @TableField(fill = FieldFill.INSERT)

    private Date createTime;

    private Long createBy;

    @TableField(fill = FieldFill.UPDATE)
    private Date modifyTime;

    private Long modifyBy;
}
