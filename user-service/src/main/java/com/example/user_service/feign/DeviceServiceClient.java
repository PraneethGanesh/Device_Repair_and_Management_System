package com.example.user_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "device-service",
        fallback = DeviceFallback.class
)
public interface DeviceServiceClient {

    @GetMapping("/api/devices/employee/{employeeId}")
    List<Object> getDevicesByEmployee(@PathVariable Long employeeId);
}
