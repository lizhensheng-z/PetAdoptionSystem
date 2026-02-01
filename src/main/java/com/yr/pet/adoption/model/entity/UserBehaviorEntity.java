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
 * 用户行为埋点表（推荐数据源）
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_behavior")
public class UserBehaviorEntity extends Model<UserBehaviorEntity> {

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
     * 宠物ID（pet.id）
     */
    @TableField("pet_id")
    private Long petId;

    /**
     * 行为类型：VIEW/FAVORITE/APPLY/SHARE
     */
    @TableField("behavior_type")
    private String behaviorType;

    /**
     * 行为权重（用于推荐）
     */
    @TableField("weight")
    private Integer weight;

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
