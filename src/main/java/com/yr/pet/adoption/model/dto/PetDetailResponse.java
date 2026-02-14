package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物详情响应数据
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "宠物详情响应数据")
public class PetDetailResponse {

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

    @Schema(description = "健康描述", example = "身体健康，已完成基础疫苗")
    private String healthDesc;

    @Schema(description = "性格描述", example = "性格亲人，活泼好动")
    private String personalityDesc;

    @Schema(description = "领养要求", example = "需要有养猫经验，定期回访")
    private String adoptRequirements;

    @Schema(description = "宠物状态", example = "PUBLISHED")
    private String status;

    @Schema(description = "经度", example = "116.404")
    private BigDecimal lng;

    @Schema(description = "纬度", example = "39.915")
    private BigDecimal lat;

    @Schema(description = "封面图片URL", example = "https://example.com/pet-cover.jpg")
    private String coverUrl;

    @Schema(description = "媒体文件列表")
    private List<PetMediaResponse> mediaList;

    @Schema(description = "宠物标签列表")
    private List<String> tags;

    @Schema(description = "发布机构用户ID", example = "1001")
    private Long orgUserId;

    @Schema(description = "发布机构名称", example = "爱心救助站")
    private String orgName;

    @Schema(description = "发布时间", example = "2024-01-20T10:00:00")
    private LocalDateTime publishedTime;

    @Schema(description = "创建时间", example = "2024-01-15T08:00:00")
    private LocalDateTime createTime;

    /**
     * 宠物媒体文件响应数据
     */
    @Data
    @Schema(description = "宠物媒体文件响应数据")
    public static class PetMediaResponse {
        @Schema(description = "媒体文件ID", example = "1")
        private Long id;

        @Schema(description = "媒体文件URL", example = "https://example.com/pet-image.jpg")
        private String url;

        @Schema(description = "媒体类型：IMAGE/VIDEO", example = "IMAGE")
        private String mediaType;

        @Schema(description = "排序值", example = "0")
        private Integer sort;
    }
}