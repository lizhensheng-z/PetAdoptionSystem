package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 待办事项列表响应
 * @author 宗平
 * @since 2026-02-17
 */
@Data
@Schema(description = "待办事项列表响应")
public class TodoListResponse {
    
    @Schema(description = "待办事项列表")
    private List<TodoItemResponse> todos;
    
    @Schema(description = "待办事项总数", example = "15")
    private Integer totalCount;
}