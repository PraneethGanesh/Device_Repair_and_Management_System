package com.example.device_service.DTO;

import java.util.UUID;

public class AssignmentRequest {
    private long deviceInstanceId;
    private UUID employeeId;

    public long getDeviceInstanceId() {
        return deviceInstanceId;
    }

    public void setDeviceInstanceId(long deviceInstanceId) {
        this.deviceInstanceId = deviceInstanceId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }
}
