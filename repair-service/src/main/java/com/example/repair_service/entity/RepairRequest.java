package com.example.repair_service.entity;

import com.example.repair_service.enums.RepairStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_requests")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long deviceInstanceId;    // which physical device needs repair

    private UUID companyId;           // which company raised this

    private UUID raisedBy;          // userId from JWT — who raised it

    private long vendorId;          // who handles the repair

    private String issueDescription;  // what is the problem

    @Enumerated(EnumType.STRING)
    private RepairStatus status;      // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    private String resolutionNotes;   // filled by vendor when resolved

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceInstanceId() {
        return deviceInstanceId;
    }

    public void setDeviceInstanceId(long deviceInstanceId) {
        this.deviceInstanceId = deviceInstanceId;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
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

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
