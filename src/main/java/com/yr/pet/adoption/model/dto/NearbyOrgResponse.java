package com.yr.pet.adoption.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 周边机构响应DTO
 */
@Data
@Schema(description = "周边机构响应")
public class NearbyOrgResponse {
    
    @Schema(description = "机构列表")
    private List<NearbyOrg> list;
    
    @Schema(description = "页码")
    private Integer pageNo;
    
    @Schema(description = "每页数量")
    private Integer pageSize;
    
    @Schema(description = "总数")
    private Long total;
    
    @Schema(description = "总页数")
    private Integer totalPages;
    
    /**
     * 周边机构信息
     */
    @Data
    @Schema(description = "周边机构信息")
    public static class NearbyOrg {
        private Long id;
        private String orgName;
        private String licenseNo;
        private String contactPhone;
        private String address;
        private Double lng;
        private Double lat;
        private Double distance;
        private Integer petCount;
        private Integer adoptionCount;
        private Double rating;
    }
}