package com.example.repair_service.dto;

public class DeviceStatusDTO {
    private String status;

    public DeviceStatusDTO() {}

    public DeviceStatusDTO(String status) {
        this.status = status;
    }

    public String getDeviceStatus() { return status; }
    public void setDeviceStatus(String status) { this.status = status; }
}
