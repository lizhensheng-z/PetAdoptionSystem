package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物媒体VO
 * @author yr
 * @since 2024-01-01
 */
@Data
@Schema(description = "宠物媒体信息")
public class PetMediaVO {

    @Schema(description = "媒体ID")
    private Long id;

    @Schema(description = "宠物ID")
    private Long petId;

    @Schema(description = "媒体URL")
    private String url;

    @Schema(description = "媒体类型")
    private String mediaType;

    @Schema(description = "媒体类型描述")
    private String mediaTypeDesc;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}