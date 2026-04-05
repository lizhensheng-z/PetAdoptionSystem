package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


import java.util.List;

/**
 * 批量删除用户请求
 */
@Data
@Schema(description = "批量删除用户请求")
public class BatchDeleteUsersRequest {

    @Schema(description = "用户ID列表", required = true)
    @NotEmpty(message = "用户ID列表不能为空")
    @JsonProperty(value = "ids")
    private List<Long> userIds;
}