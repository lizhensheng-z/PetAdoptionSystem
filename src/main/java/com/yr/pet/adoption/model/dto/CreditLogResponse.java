package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 信用日志响应DTO
 * 用于返回用户信用积分的变更记录
 * 
 * @author 宗平
 * @since 2026-02-18
 */
@Data
@Schema(description = "信用日志响应")
public class CreditLogResponse {

    @Schema(description = "记录ID", example = "12345")
    private Long id;

    @Schema(description = "用户ID", example = "789")
    private Long userId;

    @Schema(description = "积分变动值（正数为增加，负数为减少）", example = "10")
    private Integer delta;

    @Schema(description = "变动原因描述", example = "每日打卡奖励")
    private String reason;

    @Schema(description = "变动类型（CHECKIN/STREAK/OVERDUE/VACCINE等）", example = "CHECKIN")
    private String type;

    @Schema(description = "关联记录ID", example = "456")
    private Long relatedId;

    @Schema(description = "关联记录类型", example = "CHECKIN_POST")
    private String relatedType;

    @Schema(description = "记录创建时间", example = "2026-02-17T20:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "变动后的积分余额", example = "850")
    private Integer balance;

    @Schema(description = "额外元数据，根据type不同而变化")
    private Map<String, Object> metadata;
}