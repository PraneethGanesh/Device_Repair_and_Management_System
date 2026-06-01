package com.example.repair_service.dto;

import jakarta.validation.constraints.NotNull;

public class CloseRepairDTO {

    @NotNull(message = "Admin ID is required")
    private long adminId;

    // Employee to assign device to after repair — can be same or different
    @NotNull(message = "Employee ID is required")
    private long assignToEmployeeId;

    public long getAdminId() { return adminId; }
    public void setAdminId(long adminId) { this.adminId = adminId; }

    public long getAssignToEmployeeId() { return assignToEmployeeId; }
    public void setAssignToEmployeeId(long assignToEmployeeId) { this.assignToEmployeeId = assignToEmployeeId; }
}
