package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;

/**
 * 机构统计概览DTO
 * @author yr
 * @since 2026-01-01
 */
public class OrgOverviewStatistics {
    
    private Integer totalPets;
    private Integer publishedPets;
    private Integer draftPets;
    private Integer adoptedPets;
    private Integer pendingAuditPets;

    public Integer getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(Integer totalPets) {
        this.totalPets = totalPets;
    }

    public Integer getPublishedPets() {
        return publishedPets;
    }

    public void setPublishedPets(Integer publishedPets) {
        this.publishedPets = publishedPets;
    }

    public Integer getDraftPets() {
        return draftPets;
    }

    public void setDraftPets(Integer draftPets) {
        this.draftPets = draftPets;
    }

    public Integer getAdoptedPets() {
        return adoptedPets;
    }

    public void setAdoptedPets(Integer adoptedPets) {
        this.adoptedPets = adoptedPets;
    }

    public Integer getPendingAuditPets() {
        return pendingAuditPets;
    }

    public void setPendingAuditPets(Integer pendingAuditPets) {
        this.pendingAuditPets = pendingAuditPets;
    }
}