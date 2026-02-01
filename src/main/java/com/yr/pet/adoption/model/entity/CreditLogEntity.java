package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 信用分变更流水表
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("credit_log")
public class CreditLogEntity extends Model<CreditLogEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
