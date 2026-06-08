package com.example.order_service.Service;

import com.example.order_service.Client.DeviceServiceClient;
import com.example.order_service.DTO.DeviceResponseDTO;
import com.example.order_service.DTO.OrderDTO;
import com.example.order_service.DTO.OrderRequest;
import com.example.order_service.Entity.Order;
import com.example.order_service.Enum.OrderStatus;
import com.example.order_service.Repository.OrderRepository;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        Order order=new Order();
        order.setCompany_id(companyId);
        order.setDevice_id(orderRequest.getDevice_id());
        order.setVendor_id(orderRequest.getVendor_id());
        order.setQuantity(orderRequest.getQuantity());
        order.setStatus(OrderStatus.REQUESTED);
        Order saved=orderRepository.save(order);
        OrderDTO orderDTO=toOrderDTO(saved);
        deviceServiceClient.addDeviceInstance(orderDTO);
        return ResponseEntity.ok("placed order for device:"+orderRequest.getDevice_id()+", Quantity:"+orderRequest.getQuantity());
    }

    private OrderDTO toOrderDTO(Order order){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setOrder_id(order.getId());
        orderDTO.setCompany_id(orderDTO.getCompany_id());
        orderDTO.setDevice_id(order.getDevice_id());
        orderDTO.setQuantity(order.getQuantity());
        return orderDTO;
    }

    public ResponseEntity<String> acceptOrder(long orderId) {
        Order order=orderRepository.findById(orderId)
                .orElseThrow(
                        ()->new RuntimeException("orderNotFound")
                );
        order.setStatus(OrderStatus.APPROVED);
        deviceServiceClient.updateDeviceInstance(order.getDevice_id());
        return ResponseEntity.ok("Accepted the order of the comapny:"+order.getCompany_id());
    }
}
