package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养申请VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "领养申请信息")
public class AdoptionApplicationVO {

    @Schema(description = "申请ID")
    private Long applicationId;

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "宠物名称")
    private String petName;

    @Schema(description = "宠物封面URL")
    private String petCoverUrl;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "申请状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @Schema(description = "预计审核时间")
    private String estimatedReviewTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "机构备注")
    private String orgRemark;

    @Schema(description = "最终决定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime decidedTime;
}