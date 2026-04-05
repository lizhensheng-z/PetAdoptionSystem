package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的申请VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "我的申请列表项")
public class MyApplicationVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "宠物名称")
    private String petName;

    @Schema(description = "宠物封面URL")
    private String petCoverUrl;

    @Schema(description = "宠物物种")
    private String petSpecies;

    @Schema(description = "宠物品种")
    private String petBreed;

    @Schema(description = "申请状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构头像")
    private String orgAvatar;
}