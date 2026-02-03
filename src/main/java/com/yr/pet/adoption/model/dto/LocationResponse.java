package com.yr.pet.adoption.model.dto;

import java.math.BigDecimal;

/**
 * 位置信息响应DTO
 * @author yr
 * @since 2024-01-01
 */
public class LocationResponse {

    private BigDecimal lng;
    private BigDecimal lat;
    private String address;

    // Getters and Setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}