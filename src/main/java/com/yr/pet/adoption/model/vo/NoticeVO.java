package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "公告信息")
public class NoticeVO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告摘要")
    private String summary;

    @Schema(description = "公告状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}