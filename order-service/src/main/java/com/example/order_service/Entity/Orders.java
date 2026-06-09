package com.example.order_service.Entity;

import com.example.order_service.Enum.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Orders {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "vendor_id")
    private long vendorId;

    @Column(name = "device_id")
    private long deviceId;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "placed_at")
    private LocalDateTime placedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public long getVendorId() { return vendorId; }
    public void setVendorId(long vendorId) { this.vendorId = vendorId; }

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }
}