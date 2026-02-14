package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 相似宠物响应DTO
 * @author yr
 * @since 2024-02-14
 */
@Data
@Schema(description = "相似宠物响应数据")
public class SimilarPetResponse {
    
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
    
    @Schema(description = "封面图片URL", example = "https://example.com/pet-cover.jpg")
    private String coverUrl;
    
    @Schema(description = "发布机构名称", example = "爱心救助站")
    private String orgName;
    
    @Schema(description = "经度", example = "116.404")
    private BigDecimal lng;
    
    @Schema(description = "纬度", example = "39.915")
    private BigDecimal lat;
    
    @Schema(description = "宠物标签列表")
    private List<String> tags;
    
    @Schema(description = "相似度分数（0-100）", example = "85")
    private Integer similarityScore;
}