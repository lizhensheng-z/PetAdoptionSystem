package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物详情VO
 * @author yr
 * @since 2024-01-01
 */
@Data
@Schema(description = "宠物详情信息")
public class PetDetailVO {

    @Schema(description = "宠物ID")
    private Long id;

    @Schema(description = "机构用户ID")
    private Long orgUserId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构头像")
    private String orgAvatar;

    @Schema(description = "宠物名称")
    private String name;

    @Schema(description = "物种")
    private String species;

    @Schema(description = "物种描述")
    private String speciesDesc;

    @Schema(description = "品种")
    private String breed;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "性别描述")
    private String genderDesc;

    @Schema(description = "年龄（月）")
    private Integer ageMonth;

    @Schema(description = "体型")
    private String size;

    @Schema(description = "体型描述")
    private String sizeDesc;

    @Schema(description = "毛色")
    private String color;

    @Schema(description = "是否绝育")
    private Boolean sterilized;

    @Schema(description = "是否疫苗")
    private Boolean vaccinated;

    @Schema(description = "是否驱虫")
    private Boolean dewormed;

    @Schema(description = "健康描述")
    private String healthDesc;

    @Schema(description = "性格描述")
    private String personalityDesc;

    @Schema(description = "领养要求")
    private String adoptRequirements;

    @Schema(description = "宠物状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "审核状态描述")
    private String auditStatusDesc;

    @Schema(description = "经度")
    private BigDecimal lng;

    @Schema(description = "纬度")
    private BigDecimal lat;

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

    @Schema(description = "媒体列表")
    private List<PetMediaVO> mediaList;

    @Schema(description = "标签列表")
    private List<TagVO> tagList;

    @Schema(description = "距离（米）")
    private Double distance;
}