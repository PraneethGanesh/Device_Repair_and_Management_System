package com.example.repair_service.feign;

import com.example.repair_service.dto.AssignmentRequestDTO;
import com.example.repair_service.dto.DeviceStatusDTO;
import com.example.repair_service.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "device-service")
public interface DeviceServiceClient {

    @PostMapping("/api/devices/assign")
    void assignDevice(@RequestBody AssignmentRequestDTO assignmentRequest);

    @GetMapping("/api/assignments/{instanceId}")
    ResponseEntity<UUID> getDeviceAssignment(@PathVariable("instanceId") long instanceId);

    @GetMapping("/api/device_instance/{instanceId}")
    ResponseEntity<ResponseDTO> getVendorId(@PathVariable("instanceId") long instanceId);
    @PutMapping("/api/device_instance/device/{status}/{instanceId}")
    ResponseEntity<?> updateDeviceStatus(@PathVariable("status") String status,
                                                             @PathVariable("instanceId") long instanceId);
}

