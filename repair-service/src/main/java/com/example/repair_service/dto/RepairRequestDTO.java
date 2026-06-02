package com.example.repair_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RepairRequestDTO {

    @NotNull(message = "Device ID is required")
    @Positive(message = "Device ID must be positive")
    private Long deviceId;

    @NotNull(message = "Raised by (employee/admin ID) is required")
    @Positive(message = "Raised by ID must be positive")
    private Long raisedBy;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    private boolean urgent = false;

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Long getRaisedBy() { return raisedBy; }
    public void setRaisedBy(Long raisedBy) { this.raisedBy = raisedBy; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }
}
