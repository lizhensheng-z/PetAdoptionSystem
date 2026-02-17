package com.yr.pet.adoption.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 救助机构资料表
 * </p>
 *
 * @author 榕
 * @since 2026-02-17
 */
@Data
@TableName("org_profile")
@Schema(name = "OrgProfileEntity", description = "救助机构资料实体")
public class OrgProfileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 机构用户ID（sys_user.id，role=ORG）
     */
    @TableField("user_id")
    @Schema(description = "机构用户ID")
    private Long userId;

    /**
     * 机构名称
     */
    @TableField("org_name")
    @Schema(description = "机构名称")
    private String orgName;

    /**
     * 机构资质/登记号（可选）
     */
    @TableField("license_no")
    @Schema(description = "机构资质/登记号")
    private String licenseNo;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    @Schema(description = "联系人电话")
    private String contactPhone;

    /**
     * 详细地址
     */
    @TableField("address")
    @Schema(description = "详细地址")
    private String address;

    /**
     * 省
     */
    @TableField("province")
    @Schema(description = "省")
    private String province;

    /**
     * 市
     */
    @TableField("city")
    @Schema(description = "市")
    private String city;

    /**
     * 区/县
     */
    @TableField("district")
    @Schema(description = "区/县")
    private String district;

    /**
     * 经度
     */
    @TableField("lng")
    @Schema(description = "经度")
    private BigDecimal lng;

    /**
     * 纬度
     */
    @TableField("lat")
    @Schema(description = "纬度")
    private BigDecimal lat;

    /**
     * 机构封面/图片URL
     */
    @TableField("cover_url")
    @Schema(description = "机构封面/图片URL")
    private String coverUrl;

    /**
     * 机构认证状态：PENDING/APPROVED/REJECTED
     */
    @TableField("verify_status")
    @Schema(description = "机构认证状态")
    private String verifyStatus;

    /**
     * 认证备注/驳回原因
     */
    @TableField("verify_remark")
    @Schema(description = "认证备注/驳回原因")
    private String verifyRemark;

    /**
     * 逻辑删除：0否1是
     */
    @TableField("deleted")
    @Schema(description = "逻辑删除")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getVerifyRemark() {
        return verifyRemark;
    }

    public void setVerifyRemark(String verifyRemark) {
        this.verifyRemark = verifyRemark;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "OrgProfileEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", orgName='" + orgName + '\'' +
                ", licenseNo='" + licenseNo + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", address='" + address + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", coverUrl='" + coverUrl + '\'' +
                ", verifyStatus='" + verifyStatus + '\'' +
                ", verifyRemark='" + verifyRemark + '\'' +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}