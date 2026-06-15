package com.example.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class EmployeeRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Password is required")
    private String password;

    private String department;

    private String designation;

    // Constructors
    public EmployeeRequest() {}

    public EmployeeRequest(UUID companyId, String email, String fullName, String password, String department, String designation) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.department = department;
        this.designation = designation;
    }

    // Getters
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPassword(String password) { this.password = password; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
}
