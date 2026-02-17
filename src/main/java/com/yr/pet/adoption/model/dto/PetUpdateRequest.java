package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新宠物档案请求DTO
 * @author yr
 * @since 2024-01-01
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetUpdateRequest {
    private Long id;
    @Size(max = 64, message = "宠物名字不能超过64字符")
    private String name;

    private String species;

    @Size(max = 64, message = "品种不能超过64字符")
    private String breed;

    private String gender;

    private Integer ageMonth;

    private String size;

    @Size(max = 32, message = "毛色不能超过32字符")
    private String color;

    private Boolean sterilized;

    private Boolean vaccinated;

    private Boolean dewormed;

    @Size(max = 1000, message = "健康描述不能超过1000字")
    private String healthDesc;

    @Size(max = 1000, message = "性格描述不能超过1000字")
    private String personalityDesc;

    @Size(max = 1000, message = "领养要求不能超过1000字")
    private String adoptRequirements;

    @JsonProperty("tagIds")
    private List<Long> tagIds;

    private BigDecimal lng;

    private BigDecimal lat;
    private String coverUrl;
    private String status;

}