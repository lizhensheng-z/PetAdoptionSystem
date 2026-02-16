package com.yr.pet.adoption.model.dto;

import java.util.List;

/**
 * 标签列表响应DTO
 * @author yr
 * @since 2026-02-15
 */
public class TagListResponse {
    
    private List<TagInfo> list;

    public TagListResponse() {}

    public TagListResponse(List<TagInfo> list) {
        this.list = list;
    }

    public List<TagInfo> getList() {
        return list;
    }

    public void setList(List<TagInfo> list) {
        this.list = list;
    }

    /**
     * 标签信息内部类
     */
    public static class TagInfo {
        private Long id;
        private String name;
        private String tagType;
        private Integer enabled;

        public TagInfo(Long id, String name, String tagType, Boolean enabled) {
            this.id = id;
            this.name = name;
            this.tagType = tagType;
            this.enabled = enabled != null && enabled ? 1 : 0;
        }

        public TagInfo(Long id, String name, String tagType, Integer enabled) {
            this.id = id;
            this.name = name;
            this.tagType = tagType;
            this.enabled = enabled;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTagType() {
            return tagType;
        }

        public void setTagType(String tagType) {
            this.tagType = tagType;
        }

        public Integer getEnabled() {
            return enabled;
        }

        public void setEnabled(Integer enabled) {
            this.enabled = enabled;
        }
    }
}