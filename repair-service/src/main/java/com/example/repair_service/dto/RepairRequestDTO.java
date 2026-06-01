package com.example.repair_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RepairRequestDTO {

    @NotNull(message = "Device ID is required")
    private long deviceId;

    @NotNull(message = "Raised by (employee/admin ID) is required")
    private long raisedBy;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    private boolean urgent = false;

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

    public long getRaisedBy() { return raisedBy; }
    public void setRaisedBy(long raisedBy) { this.raisedBy = raisedBy; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }
}
