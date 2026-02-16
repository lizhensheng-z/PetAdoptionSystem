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
 * 救助机构资料表
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("org_profile")
public class OrgProfileEntity extends Model<OrgProfileEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构用户ID（sys_user.id，role=ORG）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 机构名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 机构资质/登记号（可选）
     */
    @TableField("license_no")
    private String licenseNo;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 省
     */
    @TableField("province")
    private String province;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 区/县
     */
    @TableField("district")
    private String district;

    /**
     * 经度
     */
    @TableField("lng")
    private BigDecimal lng;

    /**
     * 纬度
     */
    @TableField("lat")
    private BigDecimal lat;

    /**
     * 机构认证状态：PENDING/APPROVED/REJECTED（可选启用）
     */
    @TableField("verify_status")
    private String verifyStatus;

    /**
     * 认证备注/驳回原因
     */
    @TableField("verify_remark")
    private String verifyRemark;

    /**
     * 认证备注/驳回原因
     */
    @TableField("cover_url")
    private String coverUrl;

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
