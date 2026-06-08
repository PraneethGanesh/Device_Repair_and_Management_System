package com.example.device_service.Entity;

import com.example.device_service.Enum.InstanceStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class DeviceInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long device_id;
    private String serialNumber;
    private long order_id;
    private UUID company_id;
    @Enumerated(EnumType.STRING)
    private InstanceStatus status;
    private LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    public UUID getCompany_id() {
        return company_id;
    }

    public void setCompany_id(UUID company_id) {
        this.company_id = company_id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }
}
