package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 回访提醒列表响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "回访提醒列表响应")
public class FollowupReminderListResponse {
    
    @Schema(description = "回访提醒列表")
    private List<FollowupReminderResponse> list;
}