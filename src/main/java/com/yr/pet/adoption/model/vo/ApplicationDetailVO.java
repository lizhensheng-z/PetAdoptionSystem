package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 申请详情VO
 * @author yr
 * @since 2024-01-01
 */
@Data
@Schema(description = "申请详情信息")
public class ApplicationDetailVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "宠物名称")
    private String petName;

    @Schema(description = "宠物封面URL")
    private String petCoverUrl;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "用户邮箱")
    private String userEmail;

    @Schema(description = "申请状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "申请问卷JSON数据")
    private Map<String, Object> questionnaire;

    @Schema(description = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "机构备注")
    private String orgRemark;

    @Schema(description = "最终决定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime decidedTime;

    @Schema(description = "是否可以取消")
    private Boolean canCancel;

    @Schema(description = "是否可以修改")
    private Boolean canModify;
}