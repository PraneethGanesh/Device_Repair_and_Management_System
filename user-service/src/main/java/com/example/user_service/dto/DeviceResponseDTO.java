package com.example.user_service.dto;

public class DeviceResponseDTO {
    private long vendorId;
    private long assignedtoId;

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getAssignedtoId() {
        return assignedtoId;
    }

    public void setAssignedtoId(long assignedtoId) {
        this.assignedtoId = assignedtoId;
    }
}
