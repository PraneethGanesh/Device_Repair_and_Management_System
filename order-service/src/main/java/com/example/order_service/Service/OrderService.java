package com.example.order_service.Service;

import com.example.order_service.Client.CustomerServiceClient;
import com.example.order_service.Client.DeviceServiceClient;
import com.example.order_service.DTO.*;
import com.example.order_service.Entity.Orders;
import com.example.order_service.Enum.OrderStatus;
import com.example.order_service.Publisher.MessageProducer;
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
    private final CustomerServiceClient customerServiceClient;
    private final MessageProducer messageProducer;

    public OrderService(OrderRepository orderRepository, DeviceServiceClient deviceServiceClient, CustomerServiceClient customerServiceClient, MessageProducer messageProducer) {
        this.orderRepository = orderRepository;
        this.deviceServiceClient = deviceServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.messageProducer = messageProducer;
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
        orders.setCompanyId(companyId);
        orders.setDeviceId(orderRequest.getDevice_id());
        orders.setVendorId(orderRequest.getVendor_id());
        orders.setQuantity(orderRequest.getQuantity());
        orders.setStatus(OrderStatus.REQUESTED);
        orders.setPlacedAt(LocalDateTime.now());
        Orders saved=orderRepository.save(orders);
        OrderDTO orderDTO=toOrderDTO(saved);
        deviceServiceClient.addDeviceInstance(orderDTO);
        return ResponseEntity.ok("placed order for device:"+orderRequest.getDevice_id()+", Quantity:"+orderRequest.getQuantity());
    }

    private OrderDTO toOrderDTO(Orders orders){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setOrder_id(orders.getId());
        orderDTO.setCompany_id(orders.getCompanyId());
        orderDTO.setDevice_id(orders.getDeviceId());
        orderDTO.setQuantity(orders.getQuantity());
        return orderDTO;
    }

    public String acceptOrder(long orderId,long vendorId) {
        Orders orders =orderRepository.findById(orderId)
                .orElseThrow(
                        ()->new RuntimeException("orderNotFound")
                );
        if(vendorId!= orders.getVendorId()){
            return "You cannot accept the orders that belongs to other vendors";
        }
        orders.setStatus(OrderStatus.APPROVED);
        orderRepository.save(orders);
        deviceServiceClient.updateDeviceInstance(orders.getId());
        CompanyResponse response=customerServiceClient.getCompanyById(orders.getCompanyId()).getBody();
        NotificationMessage notificationMessage=new NotificationMessage();
        notificationMessage.setEventType("Order Approved");
        notificationMessage.setRecipientEmail(response.getEmail());
        notificationMessage.setTitle("Order approved by vendor:"+vendorId);
        notificationMessage.setBody("Dear Customer,\n\n" +
                "Your order has been approved by Vendor ID: " + vendorId + ".\n\n" +
                "Order Details:\n" +
                "Order ID: " + orders.getId() + "\n" +
                "Device ID: " + orders.getDeviceId() + "\n" +
                "Quantity: " + orders.getQuantity() + "\n\n" +
                "The order is now being processed.\n\n" +
                "Thank you.");
        messageProducer.publishCompanyMessage(notificationMessage);
        return "Accepted the order of the company:"+ orders.getCompanyId();
    }

    public List<OrderDTO> getOrdersByVendor(long vendorId) {
        List<Orders> orders=orderRepository.findOrders(vendorId,OrderStatus.REQUESTED);
        List<OrderDTO> orderDTOS=orders.stream().map(orders1 -> toOrderDTO(orders1))
                .toList();
        return orderDTOS;
    }
}
