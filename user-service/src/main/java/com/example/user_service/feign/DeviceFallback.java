package com.example.user_service.feign;

import com.example.user_service.dto.AssignmentRequest;
import com.example.user_service.dto.DeviceDTO;
import org.springframework.http.ResponseEntity;
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

    @Override
    public ResponseEntity<DeviceDTO> assignDevice(AssignmentRequest assignmentRequest) {
        return null;
    }

    @Override
    public long deviceOwner(long id) {
        return 0;
    }
}
