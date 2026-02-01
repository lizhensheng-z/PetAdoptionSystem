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
 * 申请状态流转日志表
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("adoption_flow_log")
public class AdoptionFlowLogEntity extends Model<AdoptionFlowLogEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请ID（adoption_application.id）
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 变更前状态
     */
    @TableField("from_status")
    private String fromStatus;

    /**
     * 变更后状态
     */
    @TableField("to_status")
    private String toStatus;

    /**
     * 操作者用户ID（机构或管理员 sys_user.id）
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 流转备注
     */
    @TableField("remark")
    private String remark;

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
