package com.example.customer_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    private String department;
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus inviteStatus = InviteStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Employee() {}

    public Employee(String email, String fullName, String department, String designation, Company company) {
        this.email = email;
        this.fullName = fullName;
        this.department = department;
        this.designation = designation;
        this.company = company;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Company getCompany() { return company; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public InviteStatus getInviteStatus() { return inviteStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setCompany(Company company) { this.company = company; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setInviteStatus(InviteStatus inviteStatus) { this.inviteStatus = inviteStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
