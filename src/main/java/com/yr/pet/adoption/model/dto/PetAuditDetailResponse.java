package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物审核详情响应DTO
 * @author yr
 * @since 2026-02-01
 */
@Data
@Schema(description = "宠物审核详情")
public class PetAuditDetailResponse implements Serializable {

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "宠物名称")
    private String name;

    @Schema(description = "宠物封面图")
    private String coverUrl;

    @Schema(description = "物种：CAT/DOG/OTHER")
    private String species;

    @Schema(description = "品种")
    private String breed;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄（月）")
    private Integer ageMonth;

    @Schema(description = "体型：S/M/L")
    private String size;

    @Schema(description = "毛色")
    private String color;

    @Schema(description = "是否绝育")
    private Boolean sterilized;

    @Schema(description = "是否疫苗")
    private Boolean vaccinated;

    @Schema(description = "是否驱虫")
    private Boolean dewormed;

    @Schema(description = "健康描述")
    private String healthDesc;

    @Schema(description = "性格描述")
    private String personalityDesc;

    @Schema(description = "领养要求")
    private String adoptRequirements;

    @Schema(description = "媒体图片列表")
    private List<String> images;

    @Schema(description = "标签列表")
    private List<String> tags;

    // 机构信息
    @Schema(description = "机构ID")
    private Long orgUserId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构联系人")
    private String contactName;

    @Schema(description = "机构联系电话")
    private String contactPhone;

    @Schema(description = "机构地址")
    private String orgAddress;

    // 审核信息
    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "提交审核时间")
    private LocalDateTime submitTime;

    @Schema(description = "宠物状态")
    private String status;
}