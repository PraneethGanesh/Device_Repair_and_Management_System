package com.example.repair_service.dto;

import com.example.repair_service.enums.RepairStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class RepairEventDTO {

    private String eventId;
    private String eventType;
    private long repairId;
    private long deviceId;
    private UUID companyId;
    private UUID raisedBy;
    private Long companyAdminId;
    private Long vendorId;
    private String issueDescription;
    private boolean urgent;
    private RepairStatus previousStatus;
    private RepairStatus newStatus;
    private LocalDateTime timestamp;
    private boolean assignedAutomatically;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getRepairId() {
        return repairId;
    }

    public void setRepairId(long repairId) {
        this.repairId = repairId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public UUID getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(UUID raisedBy) {
        this.raisedBy = raisedBy;
    }

    public Long getCompanyAdminId() {
        return companyAdminId;
    }

    public void setCompanyAdminId(Long companyAdminId) {
        this.companyAdminId = companyAdminId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public RepairStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RepairStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public RepairStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RepairStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAssignedAutomatically() {
        return assignedAutomatically;
    }

    public void setAssignedAutomatically(boolean assignedAutomatically) {
        this.assignedAutomatically = assignedAutomatically;
    }
}