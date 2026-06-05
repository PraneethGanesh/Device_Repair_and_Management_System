package com.dms.customerservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class EmployeeRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String department;

    private String designation;

    // Constructors
    public EmployeeRequest() {}

    public EmployeeRequest(UUID companyId, String email, String fullName, String department, String designation) {
        this.companyId = companyId;
        this.email = email;
        this.fullName = fullName;
        this.department = department;
        this.designation = designation;
    }

    // Getters
    public UUID getCompanyId() { return companyId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }

    // Setters
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
}
