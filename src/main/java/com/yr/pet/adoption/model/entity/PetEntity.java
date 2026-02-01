package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 宠物档案表
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("pet")
public class PetEntity extends Model<PetEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布机构用户ID（sys_user.id，role=ORG）
     */
    @TableField("org_user_id")
    private Long orgUserId;

    /**
     * 宠物名字/昵称
     */
    @TableField("name")
    private String name;

    /**
     * 物种：CAT/DOG/OTHER
     */
    @TableField("species")
    private String species;

    /**
     * 品种
     */
    @TableField("breed")
    private String breed;

    /**
     * 性别：MALE/FEMALE/UNKNOWN
     */
    @TableField("gender")
    private String gender;

    /**
     * 年龄（月）
     */
    @TableField("age_month")
    private Integer ageMonth;

    /**
     * 体型：S/M/L
     */
    @TableField("size")
    private String size;

    /**
     * 毛色/颜色
     */
    @TableField("color")
    private String color;

    /**
     * 是否绝育：0否1是
     */
    @TableField("sterilized")
    private Byte sterilized;

    /**
     * 是否疫苗：0否1是
     */
    @TableField("vaccinated")
    private Byte vaccinated;

    /**
     * 是否驱虫：0否1是
     */
    @TableField("dewormed")
    private Byte dewormed;

    /**
     * 健康描述
     */
    @TableField("health_desc")
    private String healthDesc;

    /**
     * 性格描述（文本）
     */
    @TableField("personality_desc")
    private String personalityDesc;

    /**
     * 领养要求（文本）
     */
    @TableField("adopt_requirements")
    private String adoptRequirements;

    /**
     * 宠物状态：DRAFT/PENDING_AUDIT/PUBLISHED/APPLYING/ADOPTED/REMOVED
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态：NONE/PENDING/APPROVED/REJECTED
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 经度（默认继承机构坐标）
     */
    @TableField("lng")
    private BigDecimal lng;

    /**
     * 纬度（默认继承机构坐标）
     */
    @TableField("lat")
    private BigDecimal lat;

    /**
     * 封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 发布时间（审核通过后写入）
     */
    @TableField("published_time")
    private LocalDateTime publishedTime;

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
