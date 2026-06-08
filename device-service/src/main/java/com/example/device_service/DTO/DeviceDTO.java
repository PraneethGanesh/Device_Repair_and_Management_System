package com.example.device_service.DTO;

import com.example.device_service.Enum.DeviceStatus;
import com.example.device_service.Enum.DeviceType;

import java.time.LocalDate;

public class DeviceDTO {
    private String DeviceName;
    private int stockQuantity;
    private DeviceType deviceType;
    private LocalDate warrantyExpiry;

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getWarrantyExpiry() {
        return warrantyExpiry;
    }

    public void setWarrantyExpiry(LocalDate warrantyExpiry) {
        this.warrantyExpiry = warrantyExpiry;
    }


}
