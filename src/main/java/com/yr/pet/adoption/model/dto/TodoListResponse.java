package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 待办事项列表响应DTO
 * @author yr
 * @since 2024-02-15
 */
public class TodoListResponse {

    private List<TodoItem> todos;
    private Integer totalCount;

    public List<TodoItem> getTodos() {
        return todos;
    }

    public void setTodos(List<TodoItem> todos) {
        this.todos = todos;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}