package com.yr.pet.adoption.model.dto;

import lombok.Data;

/**
 * 机构信息DTO
 * @author yr
 * @since 2026-01-01
 */
@Data
public class OrgInfo {

    private Long id;
    private String orgName;
    private String contactPhone;
    private String address;
    private Long userId;
    private String coverUrl;

    private String VerifyStatus;
    private String City;
    private String District;

}