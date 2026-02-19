package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 信用日志实体类
 * 对应数据库表：credit_log
 * 
 * @author 宗平
 * @since 2024-02-18
 */
@Data
@TableName("credit_log")
public class CreditLogEntity {

    /**
     * 主键ID
     */
    @TableId("id")
    private Long id;

    /**
     * 用户ID（sys_user.id）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分数变化（可正可负）
     */
    @TableField("delta")
    private Integer delta;

    /**
     * 变更原因（如CHECKIN/VIOLATION）
     */
    @TableField("reason")
    private String reason;

    /**
     * 关联类型（checkin/application等）
     */
    @TableField("ref_type")
    private String refType;

    /**
     * 关联ID
     */
    @TableField("ref_id")
    private Long refId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}