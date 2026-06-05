package com.dms.customerservice.dto.response;

import com.dms.customerservice.entity.Employee;
import com.dms.customerservice.entity.InviteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmployeeResponse {

    private UUID id;
    private UUID userId;
    private UUID companyId;
    private String email;
    private String fullName;
    private String department;
    private String designation;
    private InviteStatus inviteStatus;
    private LocalDateTime createdAt;

    // Constructors
    public EmployeeResponse() {}

    public EmployeeResponse(UUID id, UUID userId, UUID companyId, String email, String fullName,
                            String department, String designation, InviteStatus inviteStatus,
                            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.email = email;
        this.fullName = fullName;
        this.department = department;
        this.designation = designation;
        this.inviteStatus = inviteStatus;
        this.createdAt = createdAt;
    }

    // Static factory method — converts entity to response
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUserId(),
                employee.getCompany().getId(),
                employee.getEmail(),
                employee.getFullName(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getInviteStatus(),
                employee.getCreatedAt()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCompanyId() { return companyId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public InviteStatus getInviteStatus() { return inviteStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setInviteStatus(InviteStatus inviteStatus) { this.inviteStatus = inviteStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
