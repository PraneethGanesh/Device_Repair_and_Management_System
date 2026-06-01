package com.example.repair_service.entity;

import com.example.repair_service.enums.RepairStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_requests")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    // Device being repaired
    @Column(nullable = false)
    private long deviceId;

    // Employee or Admin who raised the request
    @Column(nullable = false)
    private long raisedBy;

    // Admin who acknowledged the request (null until acknowledged)
    private Long adminId;

    // Vendor who self-assigned (null until vendor picks it up)
    private Long vendorId;

    @Column(nullable = false)
    private String issueDescription;

    // If urgent, vendors can see even in PENDING status
    @Column(nullable = false)
    private boolean urgent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus status = RepairStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

    public long getRaisedBy() { return raisedBy; }
    public void setRaisedBy(long raisedBy) { this.raisedBy = raisedBy; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
