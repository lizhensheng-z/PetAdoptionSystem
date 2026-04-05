package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信用账户实体类
 * 对应数据库表：credit_account
 * 
 * @author 宗平
 * @since 2026-02-18
 */
@Data
@TableName("credit_account")
public class CreditAccountEntity {

    /**
     * 用户ID（sys_user.id）
     */
    @TableId("user_id")
    private Long userId;

    /**
     * 信用分
     */
    @TableField("score")
    private Integer score;

    /**
     * 信用等级
     */
    @TableField("level")
    private Integer level;

    /**
     * 最近一次结算时间
     */
    @TableField("last_calc_time")
    private LocalDateTime lastCalcTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}