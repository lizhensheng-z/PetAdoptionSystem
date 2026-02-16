package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.List;

/**
 * 批量更新用户状态请求
 */
@Data
@Schema(description = "批量更新用户状态请求")
public class BatchUpdateStatusRequest {

    @Schema(description = "用户ID列表", required = true)
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;

    @Schema(description = "新状态：NORMAL/BANNED", required = true, example = "BANNED")
    @NotNull(message = "状态不能为空")
    private String status;
}