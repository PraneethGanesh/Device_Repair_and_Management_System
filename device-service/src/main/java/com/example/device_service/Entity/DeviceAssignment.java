package com.example.device_service.Entity;

import com.example.device_service.Enum.AssignmentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class DeviceAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long device_instance_id;
    private long company_id;
    private long employee_id;
    private AssignmentStatus status;
    private LocalDateTime assigned_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDevice_instance_id() {
        return device_instance_id;
    }

    public void setDevice_instance_id(long device_instance_id) {
        this.device_instance_id = device_instance_id;
    }

    public long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(long company_id) {
        this.company_id = company_id;
    }

    public long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(long employee_id) {
        this.employee_id = employee_id;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssigned_at() {
        return assigned_at;
    }

    public void setAssigned_at(LocalDateTime assigned_at) {
        this.assigned_at = assigned_at;
    }
}
