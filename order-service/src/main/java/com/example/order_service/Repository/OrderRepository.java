package com.example.order_service.Repository;

import com.example.order_service.Entity.Orders;
import com.example.order_service.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    @Query("""
       SELECT o
       FROM Orders o
       WHERE o.vendor_id = :vendorId
       AND o.status = :status
       """)
    List<Orders> findOrders(
            @Param("vendorId") long vendorId,
            @Param("status") OrderStatus status);
}
