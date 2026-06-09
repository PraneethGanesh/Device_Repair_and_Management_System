package com.example.order_service.Service;

import com.example.order_service.Client.DeviceServiceClient;
import com.example.order_service.DTO.DeviceResponseDTO;
import com.example.order_service.DTO.OrderDTO;
import com.example.order_service.DTO.OrderRequest;
import com.example.order_service.Entity.Orders;
import com.example.order_service.Enum.OrderStatus;
import com.example.order_service.Repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final DeviceServiceClient deviceServiceClient;

    public OrderService(OrderRepository orderRepository, DeviceServiceClient deviceServiceClient) {
        this.orderRepository = orderRepository;
        this.deviceServiceClient = deviceServiceClient;
    }

    public ResponseEntity<String> placeOrder(OrderRequest orderRequest, UUID companyId) {
        ResponseEntity<DeviceResponseDTO> deviceResponseDTOResponseEntity=
                deviceServiceClient.getDeviceById(orderRequest.getDevice_id());
        DeviceResponseDTO deviceResponseDTO=deviceResponseDTOResponseEntity.getBody();
        if(deviceResponseDTO.getVendorId()!= orderRequest.getVendor_id()){
            return ResponseEntity.ok("Device is not owned by the vendor:"+orderRequest.getVendor_id());
        }
        if(deviceResponseDTO.getStockQuantity()< orderRequest.getQuantity()){
            return ResponseEntity.ok("Insufficient Stock!!");
        }
        Orders orders =new Orders();
        orders.setCompany_id(companyId);
        orders.setDevice_id(orderRequest.getDevice_id());
        orders.setVendor_id(orderRequest.getVendor_id());
        orders.setQuantity(orderRequest.getQuantity());
        orders.setStatus(OrderStatus.REQUESTED);
        orders.setPlaced_at(LocalDateTime.now());
        Orders saved=orderRepository.save(orders);
        OrderDTO orderDTO=toOrderDTO(saved);
        deviceServiceClient.addDeviceInstance(orderDTO);
        return ResponseEntity.ok("placed order for device:"+orderRequest.getDevice_id()+", Quantity:"+orderRequest.getQuantity());
    }

    private OrderDTO toOrderDTO(Orders orders){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setOrder_id(orders.getId());
        orderDTO.setCompany_id(orders.getCompany_id());
        orderDTO.setDevice_id(orders.getDevice_id());
        orderDTO.setQuantity(orders.getQuantity());
        return orderDTO;
    }

    public String acceptOrder(long orderId,long vendorId) {
        Orders orders =orderRepository.findById(orderId)
                .orElseThrow(
                        ()->new RuntimeException("orderNotFound")
                );
        if(vendorId!= orders.getVendor_id()){
            return "You cannot accept the orders that belongs to other vendors";
        }
        orders.setStatus(OrderStatus.APPROVED);
        orderRepository.save(orders);
        deviceServiceClient.updateDeviceInstance(orders.getId());
        return "Accepted the order of the company:"+ orders.getCompany_id();
    }

    public List<OrderDTO> getOrdersByVendor(long vendorId) {
        List<Orders> orders=orderRepository.findByVendorIdAndStatus(vendorId,OrderStatus.REQUESTED);
        List<OrderDTO> orderDTOS=orders.stream().map(orders1 -> toOrderDTO(orders1))
                .toList();
        return orderDTOS;
    }
}
