package com.example.user_service.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class DeviceFallback implements DeviceServiceClient {

    @Override
    public List<Object> getDevicesByEmployee(Long employeeId) {
        // Device service is down — return empty list gracefully
        return Collections.emptyList();
    }
}
