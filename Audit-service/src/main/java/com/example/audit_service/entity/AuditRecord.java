package com.example.audit_service.entity;

import com.example.audit_service.dto.RepairEventDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class AuditRecord {

    @Id
    @GeneratedValue
    private long AuditId;
    private String eventId;
    private String eventType;

    private Long repairId;
    private Long deviceId;
    private Long companyId;
    private Long raisedBy;
    private Long companyAdminId;
    private Long vendorId;

    @Column(length = 1000)
    private String issueDescription;

    private boolean urgent;
    private String previousStatus;
    private String newStatus;
    private boolean assignedAutomatically;
    private LocalDateTime timestamp;

    public long getAuditId() {
        return AuditId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Long getRepairId() {
        return repairId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getRaisedBy() {
        return raisedBy;
    }

    public Long getCompanyAdminId() {
        return companyAdminId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public boolean isAssignedAutomatically() {
        return assignedAutomatically;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setAuditId(long auditId) {
        AuditId = auditId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setRepairId(Long repairId) {
        this.repairId = repairId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setRaisedBy(Long raisedBy) {
        this.raisedBy = raisedBy;
    }

    public void setCompanyAdminId(Long companyAdminId) {
        this.companyAdminId = companyAdminId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public void setAssignedAutomatically(boolean assignedAutomatically) {
        this.assignedAutomatically = assignedAutomatically;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    //-- helper method
    public static AuditRecord from(RepairEventDTO dto) {
        AuditRecord record = new AuditRecord();
        record.eventId               = dto.getEventId();
        record.eventType             = dto.getEventType();
        record.repairId              = dto.getRepairId();
        record.deviceId              = dto.getDeviceId();
        record.companyId             = dto.getCompanyId();
        record.raisedBy              = dto.getRaisedBy();
        record.companyAdminId        = dto.getCompanyAdminId();
        record.vendorId              = dto.getVendorId();
        record.assignedAutomatically = dto.isAssignedAutomatically();
        record.issueDescription      = dto.getIssueDescription();
        record.urgent                = dto.isUrgent();
        record.previousStatus        = dto.getPreviousStatus();
        record.newStatus             = dto.getNewStatus();
        record.timestamp             = dto.getTimestamp();
        return record;
    }
}

