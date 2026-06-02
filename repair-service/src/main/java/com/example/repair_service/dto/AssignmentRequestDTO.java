package com.example.repair_service.dto;

public class AssignmentRequestDTO {

    private long deviceId;
    private long userId;

    public AssignmentRequestDTO() {}

    public AssignmentRequestDTO(long deviceId, long userId) {
        this.deviceId = deviceId;
        this.userId = userId;
    }

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
}
