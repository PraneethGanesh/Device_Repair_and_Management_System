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
    private UUID company_id;
    private long vendor_id;
    private long device_id;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime placed_at;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }



    public long getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(long vendor_id) {
        this.vendor_id = vendor_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getCompany_id() {
        return company_id;
    }

    public void setCompany_id(UUID company_id) {
        this.company_id = company_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public LocalDateTime getPlaced_at() {
        return placed_at;
    }

    public void setPlaced_at(LocalDateTime placed_at) {
        this.placed_at = placed_at;
    }
}
