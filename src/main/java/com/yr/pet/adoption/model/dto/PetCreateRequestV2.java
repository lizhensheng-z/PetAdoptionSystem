package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建宠物档案请求DTO V2 - 支持嵌套结构
 * @author yr
 * @since 2024-02-16
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetCreateRequestV2 {

    @Size(max = 64, message = "宠物名字不能超过64字符")
    private String name;

    @NotNull(message = "物种不能为空")
    private String species;

    @Size(max = 64, message = "品种不能超过64字符")
    @NotNull(message = "品种不能为空")
    private String breed;

    @NotNull(message = "性别不能为空")
    private String gender;

    @NotNull(message = "年龄不能为空")
    private Integer ageMonths;
    private Integer ageYears;

    @JsonProperty("ageMonth")
    private Integer ageMonth;

    private String size;

    @Size(max = 32, message = "毛色不能超过32字符")
    private String color;

    private Location location;

    private Health health;

    private Personality personality;

    @Size(max = 1000, message = "领养要求不能超过1000字")
    private String adoptRequirements;

    private String status;

    private String auditStatus;

    private String coverUrl;

    // 内部类定义
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String address;
        private String city;
        private String district;
        private BigDecimal lng;
        private BigDecimal lat;

        // Getters and Setters
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public BigDecimal getLng() {
            return lng;
        }

        public void setLng(BigDecimal lng) {
            this.lng = lng;
        }

        public BigDecimal getLat() {
            return lat;
        }

        public void setLat(BigDecimal lat) {
            this.lat = lat;
        }
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Health {
        private Boolean sterilized;
        private Boolean vaccinated;
        private Boolean dewormed;
        
        @JsonProperty("healthDesc")
        private String healthDesc;

        // Getters and Setters
        public Boolean getSterilized() {
            return sterilized;
        }

        public void setSterilized(Boolean sterilized) {
            this.sterilized = sterilized;
        }

        public Boolean getVaccinated() {
            return vaccinated;
        }

        public void setVaccinated(Boolean vaccinated) {
            this.vaccinated = vaccinated;
        }

        public Boolean getDewormed() {
            return dewormed;
        }

        public void setDewormed(Boolean dewormed) {
            this.dewormed = dewormed;
        }

        public String getHealthDesc() {
            return healthDesc;
        }

        public void setHealthDesc(String healthDesc) {
            this.healthDesc = healthDesc;
        }
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Personality {
        @JsonProperty("desc")
        private String desc;

        @JsonProperty("tags")
        private List<Long> tags;

        // Getters and Setters
        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<Long> getTags() {
            return tags;
        }

        public void setTags(List<Long> tags) {
            this.tags = tags;
        }
    }

}