package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待审核机构列表响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "待审核机构列表响应")
public class PendingOrgResponse implements Serializable {

    @Schema(description = "机构用户ID")
    private Long id;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "机构地址")
    private String address;

    @Schema(description = "机构简介")
    private String description;

    @Schema(description = "认证状态")
    private String verifyStatus;

    @Schema(description = "认证备注")
    private String verifyRemark;
}