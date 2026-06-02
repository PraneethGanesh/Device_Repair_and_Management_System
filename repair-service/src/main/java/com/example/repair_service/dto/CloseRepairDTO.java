package com.example.repair_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CloseRepairDTO {

    @NotNull(message = "Admin ID is required")
    @Positive(message = "Admin ID must be positive")
    private Long adminId;

    // Employee to assign device to after repair — can be same or different
    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    private Long assignToEmployeeId;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getAssignToEmployeeId() { return assignToEmployeeId; }
    public void setAssignToEmployeeId(Long assignToEmployeeId) { this.assignToEmployeeId = assignToEmployeeId; }
}
