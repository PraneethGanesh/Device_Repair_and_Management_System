package com.example.customer_service.dto;

import com.example.customer_service.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderDTO {

    private UUID company_id;
    private long order_id;
    private long device_id;
    private long vendor_id;
    private int quantity;
    private OrderStatus status;
    private LocalDateTime placedAt;

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

    public long getDeviceId() {
        return device_id;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public long getOrderId() {
        return order_id;
    }

    public UUID getCompany_id() {
        return company_id;
    }

    public void setCompany_id(UUID company_id) {
        this.company_id = company_id;
    }

    public UUID getCompanyId() {
        return company_id;
    }

    public long getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(long vendor_id) {
        this.vendor_id = vendor_id;
    }

    public long getVendorId() {
        return vendor_id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }
}
