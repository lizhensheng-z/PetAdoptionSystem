package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构宠物列表VO
 * @author yr
 * @since 2024-01-01
 */
@Data
@Schema(description = "机构宠物列表项")
public class OrgPetListVO {

    @Schema(description = "宠物ID")
    private Long id;

    @Schema(description = "宠物名称")
    private String name;

    @Schema(description = "物种")
    private String species;

    @Schema(description = "品种")
    private String breed;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄（月）")
    private Integer ageMonth;

    @Schema(description = "体型")
    private String size;

    @Schema(description = "毛色")
    private String color;

    @Schema(description = "宠物状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "审核状态描述")
    private String auditStatusDesc;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "申请数量")
    private Integer applicationCount;

    @Schema(description = "标签列表")
    private List<TagVO> tagList;
}