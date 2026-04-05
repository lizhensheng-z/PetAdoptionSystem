package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 最近申请列表响应DTO
 * @author yr
 * @since 2026-02-15
 */
public class RecentApplicationListResponse {

    private List<RecentApplicationItem> list;

    public List<RecentApplicationItem> getList() {
        return list;
    }

    public void setList(List<RecentApplicationItem> list) {
        this.list = list;
    }
}