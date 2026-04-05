package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物列表响应数据
 * @author yr
 * @since 2026-02-14
 */
@Data
@Schema(description = "宠物列表响应数据")
public class PetListResponse {

    @Schema(description = "宠物ID", example = "1")
    private Long id;

    @Schema(description = "宠物名称", example = "小橘")
    private String name;

    @Schema(description = "物种：CAT/DOG/OTHER", example = "CAT")
    private String species;

    @Schema(description = "品种", example = "橘猫")
    private String breed;

    @Schema(description = "年龄（月）", example = "6")
    private Integer ageMonth;

    @Schema(description = "性别：MALE/FEMALE/UNKNOWN", example = "MALE")
    private String gender;

    @Schema(description = "体型：S/M/L", example = "M")
    private String size;

    @Schema(description = "毛色", example = "橘色")
    private String color;

    @Schema(description = "是否绝育", example = "false")
    private Boolean sterilized;

    @Schema(description = "是否疫苗", example = "true")
    private Boolean vaccinated;

    @Schema(description = "是否驱虫", example = "true")
    private Boolean dewormed;

    @Schema(description = "宠物状态", example = "PUBLISHED")
    private String status;

    @Schema(description = "封面图片URL", example = "https://example.com/pet-cover.jpg")
    private String coverUrl;

    @Schema(description = "宠物图片列表")
    private List<String> images;

    @Schema(description = "宠物标签列表")
    private List<String> tags;

    @Schema(description = "发布机构用户ID", example = "1001")
    private Long orgUserId;

    @Schema(description = "发布机构名称", example = "爱心救助站")
    private String orgName;

    @Schema(description = "发布时间", example = "2026-01-20T10:00:00")
    private LocalDateTime publishedTime;

    @Schema(description = "距离（公里，当提供经纬度时计算）", example = "5.2")
    private BigDecimal distance;

    @Schema(description = "匹配分数（用于推荐功能）", example = "85")
    private Integer matchScore;
}