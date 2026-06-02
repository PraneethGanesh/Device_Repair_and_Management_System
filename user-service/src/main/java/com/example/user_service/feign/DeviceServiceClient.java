package com.example.user_service.feign;

import com.example.user_service.dto.AssignmentRequest;
import com.example.user_service.dto.DeviceDTO;
import com.example.user_service.dto.DeviceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "device-service",
        fallback = DeviceFallback.class
)
public interface DeviceServiceClient {

    @GetMapping("/api/devices/employee/{employeeId}")
    List<Object> getDevicesByEmployee(@PathVariable Long employeeId);

    @PostMapping("/api/devices/assign")
    ResponseEntity<DeviceDTO> assignDevice(@RequestBody AssignmentRequest assignmentRequest);

    @GetMapping("/api/devices/owner/{id}")
    DeviceResponseDTO deviceOwner(@PathVariable long id);

}
