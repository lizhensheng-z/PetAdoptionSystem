package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 机构资料请求DTO
 * @author 榕
 * @since 2026-02-17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "OrgProfileRequest", description = "机构资料请求")
public class OrgProfileRequest {

    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 128, message = "机构名称长度不能超过128个字符")
    @Schema(description = "机构名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgName;

    /**
     * 机构资质/登记号（可选）
     */
    @Size(max = 64, message = "机构资质/登记号长度不能超过64个字符")
    @Schema(description = "机构资质/登记号")
    private String licenseNo;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 64, message = "联系人姓名长度不能超过64个字符")
    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    /**
     * 联系人电话
     */
    @NotBlank(message = "联系人电话不能为空")
    @Size(max = 32, message = "联系人电话长度不能超过32个字符")
    @Schema(description = "联系人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactPhone;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址长度不能超过255个字符")
    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    /**
     * 省
     */
    @NotBlank(message = "省不能为空")
    @Size(max = 64, message = "省长度不能超过64个字符")
    @Schema(description = "省", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    /**
     * 市
     */
    @NotBlank(message = "市不能为空")
    @Size(max = 64, message = "市长度不能超过64个字符")
    @Schema(description = "市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    @Size(max = 64, message = "区/县长度不能超过64个字符")
    @Schema(description = "区/县", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private BigDecimal lng;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private BigDecimal lat;

    /**
     * 机构封面/图片URL
     */
    @Size(max = 512, message = "机构封面/图片URL长度不能超过512个字符")
    @Schema(description = "机构封面/图片URL")
    private String coverUrl;

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
}