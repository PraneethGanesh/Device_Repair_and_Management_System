package com.example.repair_service.dto;

import jakarta.persistence.Column;

public class ResponseDTO {
    private long requestId;
    private long deviceId;
    private long raisedBy;
    private Long adminId;
    private Long vendorId;
    private String issueDescription;
    private String status;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(long raisedBy) {
        this.raisedBy = raisedBy;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
