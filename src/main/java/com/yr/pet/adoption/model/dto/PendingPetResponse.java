package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待审核宠物响应DTO
 * @author yr
 * @since 2026-02-01
 */
@Data
@Schema(description = "待审核宠物信息")
public class PendingPetResponse implements Serializable {

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "宠物名称")
    private String petName;

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

    @Schema(description = "机构ID")
    private Long orgUserId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构联系人")
    private String contactName;

    @Schema(description = "机构联系电话")
    private String contactPhone;

    @Schema(description = "提交审核时间")
    private LocalDateTime submitTime;

    @Schema(description = "审核状态：PENDING/APPROVED/REJECTED")
    private String auditStatus;
}