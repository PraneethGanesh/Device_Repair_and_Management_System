package com.example.customer_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String companyName;

    private String email;

    @Column(unique = true)
    private String gstNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    // Constructors
    public Company() {}

    public Company(String userId, String companyName, String gstNumber, String address) {
        this.userId = userId;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.address = address;
    }

    public Company(String userId, String companyName, String email, String gstNumber, String address) {
        this.userId = userId;
        this.companyName = companyName;
        this.email = email;
        this.gstNumber = gstNumber;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters
    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getCompanyName() { return companyName; }
    public String getGstNumber() { return gstNumber; }
    public String getAddress() { return address; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Employee> getEmployees() { return employees; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
