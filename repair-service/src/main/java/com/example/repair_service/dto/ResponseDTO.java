package com.example.repair_service.dto;

import jakarta.persistence.Column;

public class ResponseDTO {
    private long vendorId;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}
