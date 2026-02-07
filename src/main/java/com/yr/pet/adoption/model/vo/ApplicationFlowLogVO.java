package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请流程日志VO
 * @author yr
 * @since 2024-01-01
 */
@Data
@Schema(description = "申请流程日志信息")
public class ApplicationFlowLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "申请ID")
    private Long applicationId;

    @Schema(description = "变更前状态")
    private String fromStatus;

    @Schema(description = "变更后状态")
    private String toStatus;

    @Schema(description = "操作者用户ID")
    private Long operatorId;

    @Schema(description = "操作者用户名")
    private String operatorName;

    @Schema(description = "流转备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}