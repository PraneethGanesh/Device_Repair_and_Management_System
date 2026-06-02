package com.example.device_service.DTO;

import com.example.device_service.Enum.DeviceStatus;

public class DeviceStatusDTO {
    private long deviceId;
    private String status;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
