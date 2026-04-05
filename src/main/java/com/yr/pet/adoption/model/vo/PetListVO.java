package com.yr.pet.adoption.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物列表VO
 * @author yr
 * @since 2026-01-01
 */
@Data
@Schema(description = "宠物列表项")
public class PetListVO {

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

    @Schema(description = "宠物状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "标签列表")
    private List<TagVO> tagList;

    @Schema(description = "距离（米）")
    private Double distance;
}