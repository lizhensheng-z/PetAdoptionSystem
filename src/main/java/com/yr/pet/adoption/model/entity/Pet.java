package com.yr.pet.adoption.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 宠物实体类 - 用于演示Swagger模型文档
 */
@Data
@Schema(description = "宠物信息实体")
public class Pet {
    
    @Schema(description = "宠物ID", example = "1")
    private Long id;
    
    @Schema(description = "宠物名称", example = "小白", required = true)
    private String name;
    
    @Schema(description = "宠物类型", example = "狗", allowableValues = {"狗", "猫", "鸟", "其他"})
    private String type;
    
    @Schema(description = "宠物年龄（月）", example = "12", minimum = "0")
    private Integer age;
    
    @Schema(description = "宠物品种", example = "金毛")
    private String breed;
    
    @Schema(description = "宠物描述", example = "活泼可爱的金毛犬，已接种疫苗")
    private String description;
    
    @Schema(description = "是否已领养", example = "false")
    private Boolean adopted;
    
    @Schema(description = "宠物图片URL", example = "https://example.com/pet1.jpg")
    private String imageUrl;
}