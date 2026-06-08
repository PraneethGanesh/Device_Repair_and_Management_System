package com.example.order_service.Client;

import com.example.order_service.DTO.DeviceResponseDTO;
import com.example.order_service.DTO.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "device-service")
public interface DeviceServiceClient {
    @GetMapping("/api/devices/{id}")
    ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable long id);
    @PostMapping("/api/device_instance")
    ResponseEntity<?> addDeviceInstance(@RequestBody OrderDTO orderDTO);
    @PutMapping("/api/device_instance/{deviceId}")
    ResponseEntity<?> updateDeviceInstance(@PathVariable long deviceId);
}
