package com.example.Audit_service.dto;

import java.time.LocalDateTime;

public class RepairEventDTO {

    private String eventId;
    private String eventType;
    private long repairId;
    private long deviceId;
    private long companyId;
    private long raisedBy;
    private Long companyAdminId;
    private Long vendorId;
    private String issueDescription;
    private boolean urgent;
    private String previousStatus;
    private String newStatus;
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

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(long raisedBy) {
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

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
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
