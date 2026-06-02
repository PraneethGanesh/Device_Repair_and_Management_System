package com.example.repair_service.feign;

import com.example.repair_service.dto.AssignmentRequestDTO;
import com.example.repair_service.dto.DeviceStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "device-service")
public interface DeviceServiceClient {

    // Update device status e.g. UNDER_REPAIR, AVAILABLE, ASSIGNED
    @PutMapping("/api/devices/{id}/status")
    void updateDeviceStatus(@PathVariable("id") long deviceId,
                            @RequestBody DeviceStatusDTO statusDTO);

    @PostMapping("/api/devices/assign")
    void assignDevice(@RequestBody AssignmentRequestDTO assignmentRequest);
}
