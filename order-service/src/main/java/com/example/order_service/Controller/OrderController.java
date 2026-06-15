package com.example.order_service.Controller;

import com.example.order_service.DTO.OrderDTO;
import com.example.order_service.DTO.OrderRequest;
import com.example.order_service.Entity.Orders;
import com.example.order_service.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{companyId}")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest, @PathVariable UUID companyId){
        return orderService.placeOrder(orderRequest,companyId);
    }

    @PutMapping("/{orderId}/{vendorId}")
    public String acceptOrder(@PathVariable long orderId,
                                              @PathVariable long vendorId){
        return orderService.acceptOrder(orderId,vendorId);
    }

    @GetMapping("/{vendorId}")
    public List<OrderDTO> getOrdersByVendor(@PathVariable long vendorId){
        return orderService.getOrdersByVendor(vendorId);
    }

    @GetMapping("/company/{companyId}")
    public List<OrderDTO> getOrdersByCompany(@PathVariable UUID companyId){
        return orderService.getOrdersByCompany(companyId);
    }

}
