package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 宠物列表查询请求参数
 * @author yr
 * @since 2026-02-14
 */
@Data
@Schema(description = "宠物列表查询请求参数")
public class PetListRequest {

    @Schema(description = "搜索关键词（匹配宠物名称、品种、性格描述）", example = "橘猫")
    private String keyword;

    @Schema(description = "物种筛选：CAT/DOG/OTHER", example = "CAT")
    private String species;

    @Schema(description = "性别筛选：MALE/FEMALE", example = "MALE")
    private String gender;

    @Schema(description = "最小年龄（月）", example = "0")
    @Min(value = 0, message = "最小年龄不能小于0")
    private Integer ageMin;

    @Schema(description = "最大年龄（月）", example = "120")
    @Min(value = 0, message = "最大年龄不能小于0")
    private Integer ageMax;

    @Schema(description = "体型筛选：S/M/L", example = "M")
    private String size;

    @Schema(description = "是否疫苗", example = "true")
    private Boolean vaccinated;

    @Schema(description = "是否绝育", example = "false")
    private Boolean sterilized;

    @Schema(description = "状态筛选（默认：PUBLISHED）", example = "PUBLISHED")
    private String status = "PUBLISHED";

    @Schema(description = "页码（从1开始）", example = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "12")
    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 48, message = "每页数量不能超过48")
    private Integer pageSize = 12;

    @Schema(description = "用户经度（用于计算距离）", example = "116.404")
    private BigDecimal lng;

    @Schema(description = "用户纬度（用于计算距离）", example = "39.915")
    private BigDecimal lat;

    @Schema(description = "最大距离（公里）", example = "50")
    @Min(value = 1, message = "最大距离必须大于等于1公里")
    @Max(value = 500, message = "最大距离不能超过500公里")
    private Integer maxDistance;

    @Schema(description = "排序字段", example = "published_time")
    private String sortBy = "published_time";

    @Schema(description = "排序方式：asc/desc", example = "desc")
    private String order = "desc";
}