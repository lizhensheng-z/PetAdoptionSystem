package com.yr.pet.adoption.model.dto;

/**
 * 机构领养统计DTO
 * @author yr
 * @since 2024-01-01
 */
public class OrgAdoptionStatistics {
    
    private Double successRate;
    private Integer averageProcessDays;
    private Integer thisMonthAdoptions;
    private Integer thisMonthApplications;

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Integer getAverageProcessDays() {
        return averageProcessDays;
    }

    public void setAverageProcessDays(Integer averageProcessDays) {
        this.averageProcessDays = averageProcessDays;
    }

    public Integer getThisMonthAdoptions() {
        return thisMonthAdoptions;
    }

    public void setThisMonthAdoptions(Integer thisMonthAdoptions) {
        this.thisMonthAdoptions = thisMonthAdoptions;
    }

    public Integer getThisMonthApplications() {
        return thisMonthApplications;
    }

    public void setThisMonthApplications(Integer thisMonthApplications) {
        this.thisMonthApplications = thisMonthApplications;
    }
}