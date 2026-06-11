package com.example.customer_service.dto;

import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.entity.Company;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanyResponse {

    private UUID id;
    private String userId;
    private String companyName;
    private String email;
    private String gstNumber;
    private String address;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;

    // Constructors
    public CompanyResponse() {}

    public CompanyResponse(UUID id, String userId, String companyName, String gstNumber,
                           String address, ApprovalStatus approvalStatus, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.address = address;
        this.approvalStatus = approvalStatus;
        this.createdAt = createdAt;
    }

    public CompanyResponse(UUID id, String userId, String companyName, String email, String gstNumber, String address, ApprovalStatus approvalStatus, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.email = email;
        this.gstNumber = gstNumber;
        this.address = address;
        this.approvalStatus = approvalStatus;
        this.createdAt = createdAt;
    }

    // Static factory method — converts entity to response
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getUserId(),
                company.getCompanyName(),
                company.getEmail(),
                company.getGstNumber(),
                company.getAddress(),
                company.getApprovalStatus(),
                company.getCreatedAt()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getCompanyName() { return companyName; }
    public String getGstNumber() { return gstNumber; }
    public String getAddress() { return address; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
