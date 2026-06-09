package com.example.order_service.Repository;

import com.example.order_service.Entity.Orders;
import com.example.order_service.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    List<Orders> findByVendorIdAndStatus(long vendorId, OrderStatus status);
}
