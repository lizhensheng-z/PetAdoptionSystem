package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 系统配置响应DTO
 */
@Data
@Schema(description = "系统配置响应")
public class SystemConfigResponse {
    
    @Schema(description = "信用等级配置")
    private List<CreditLevel> creditLevels;
    
    @Schema(description = "宠物种类")
    private List<DictItem> petSpecies;
    
    @Schema(description = "宠物品种")
    private Map<String, List<String>> petBreeds;
    
    @Schema(description = "宠物体型")
    private List<DictItem> petSizes;
    
    @Schema(description = "性别选项")
    private List<DictItem> genders;
    
    @Schema(description = "申请状态")
    private List<ApplicationStatus> applicationStatuses;
    
    @Schema(description = "标签分类")
    private List<TagCategory> tagCategories;
    
    @Schema(description = "省份列表")
    private List<String> provinces;
    
    /**
     * 信用等级
     */
    @Data
    @Schema(description = "信用等级")
    public static class CreditLevel {
        private Integer level;
        private Integer minScore;
        private Integer maxScore;
        private String name;
        private String icon;
    }
    
    /**
     * 字典项
     */
    @Data
    @Schema(description = "字典项")
    public static class DictItem {
        private String value;
        private String label;
    }
    
    /**
     * 申请状态
     */
    @Data
    @Schema(description = "申请状态")
    public static class ApplicationStatus {
        private String value;
        private String label;
        private Integer step;
    }
    
    /**
     * 标签分类
     */
    @Data
    @Schema(description = "标签分类")
    public static class TagCategory {
        private String type;
        private String label;
    }
}