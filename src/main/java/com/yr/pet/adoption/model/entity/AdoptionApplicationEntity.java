package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 领养申请表
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("adoption_application")
public class AdoptionApplicationEntity extends Model<AdoptionApplicationEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宠物ID（pet.id）
     */
    @TableField("pet_id")
    private Long petId;

    /**
     * 领养人用户ID（sys_user.id，role=USER）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 申请问卷JSON
     */
    @TableField("questionnaire_json")
    private String questionnaireJson;

    /**
     * 申请状态：SUBMITTED/UNDER_REVIEW/INTERVIEW/HOME_VISIT/APPROVED/REJECTED/CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 拒绝原因（机构填写）
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 机构备注
     */
    @TableField("org_remark")
    private String orgRemark;

    /**
     * 最终决定时间（通过/拒绝）
     */
    @TableField("decided_time")
    private LocalDateTime decidedTime;

    /**
     * 逻辑删除：0否1是
     */
    @TableField("deleted")
    @TableLogic
    private Byte deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
