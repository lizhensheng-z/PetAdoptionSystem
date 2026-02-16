package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 最近宠物列表响应DTO
 * @author yr
 * @since 2024-02-15
 */
public class RecentPetListResponse {

    private List<RecentPetItem> list;

    public List<RecentPetItem> getList() {
        return list;
    }

    public void setList(List<RecentPetItem> list) {
        this.list = list;
    }
}