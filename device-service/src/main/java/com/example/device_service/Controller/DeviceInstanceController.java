package com.example.device_service.Controller;

import com.example.device_service.DTO.OrderDTO;
import com.example.device_service.Service.DeviceInstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device_instance")
public class DeviceInstanceController {
    private final DeviceInstanceService deviceInstanceService;

    public DeviceInstanceController(DeviceInstanceService deviceInstanceService) {
        this.deviceInstanceService = deviceInstanceService;
    }

    @PostMapping
    public ResponseEntity<?> addDeviceInstance(@RequestBody OrderDTO orderDTO){
        return deviceInstanceService.addDeviceInstance(orderDTO);
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<?> updateDeviceInstance(@PathVariable long deviceId){
        return deviceInstanceService.updateDeviceInstance(deviceId);
    }
}
