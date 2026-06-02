package com.example.repair_service.dto;

public class DeviceStatusDTO {
    private String status;

    public DeviceStatusDTO() {}

    public DeviceStatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
