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
 * 审计日志表（关键操作留痕）
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("audit_log")
public class AuditLogEntity extends Model<AuditLogEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作者用户ID（sys_user.id）
     */
    @TableField("actor_id")
    private Long actorId;

    /**
     * 动作（如BAN_USER/REVIEW_PET/DELETE_PET）
     */
    @TableField("action")
    private String action;

    /**
     * 目标类型（USER/PET/APPLICATION/CONFIG等）
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 目标ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 操作详情JSON
     */
    @TableField("detail_json")
    private String detailJson;

    /**
     * 操作者IP
     */
    @TableField("ip")
    private String ip;

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
