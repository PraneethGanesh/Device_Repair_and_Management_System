package com.example.user_service.dto;

import com.example.user_service.entity.DeviceType;

import java.time.LocalDate;

public class DeviceDTO {
    private String DeviceName;
    private DeviceType deviceType;
    private LocalDate warrantyExpiry;

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public LocalDate getWarrantyExpiry() {
        return warrantyExpiry;
    }

    public void setWarrantyExpiry(LocalDate warrantyExpiry) {
        this.warrantyExpiry = warrantyExpiry;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
