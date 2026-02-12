package com.yr.pet.adoption.model.dto;

/**
 * 机构统计数据响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class OrgStatisticsResponse {
    
    private OrgOverviewStatistics overview;
    private OrgApplicationStatistics applications;
    private OrgAdoptionStatistics adoption;
    private OrgCredibilityStatistics credibility;

    public OrgOverviewStatistics getOverview() {
        return overview;
    }

    public void setOverview(OrgOverviewStatistics overview) {
        this.overview = overview;
    }

    public OrgApplicationStatistics getApplications() {
        return applications;
    }

    public void setApplications(OrgApplicationStatistics applications) {
        this.applications = applications;
    }

    public OrgAdoptionStatistics getAdoption() {
        return adoption;
    }

    public void setAdoption(OrgAdoptionStatistics adoption) {
        this.adoption = adoption;
    }

    public OrgCredibilityStatistics getCredibility() {
        return credibility;
    }

    public void setCredibility(OrgCredibilityStatistics credibility) {
        this.credibility = credibility;
    }
}