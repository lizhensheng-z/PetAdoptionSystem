package com.yr.pet.adoption.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 创建打卡请求DTO
 * @author yr
 * @since 2026-01-01
 */
public class CheckinCreateRequest {

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @Size(max = 2000, message = "打卡内容最多2000字")
    private String content;

    private List<String> mediaUrls;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}