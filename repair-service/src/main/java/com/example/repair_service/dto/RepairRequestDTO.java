package com.example.repair_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class RepairRequestDTO {
  private long deviceInstanceId;
  private String issueDescription;

    public long getDeviceInstanceId() {
        return deviceInstanceId;
    }

    public void setDeviceInstanceId(long deviceInstanceId) {
        this.deviceInstanceId = deviceInstanceId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
