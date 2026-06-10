package com.example.device_service.Controller;

import com.example.device_service.DTO.OrderDTO;
import com.example.device_service.DTO.ResponseDTO;
import com.example.device_service.Entity.DeviceInstance;
import com.example.device_service.Service.DeviceInstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/device_instance")
public class DeviceInstanceController {
    private final DeviceInstanceService deviceInstanceService;

    public DeviceInstanceController(DeviceInstanceService deviceInstanceService) {
        this.deviceInstanceService = deviceInstanceService;
    }

    @PostMapping
    public ResponseEntity<String> addDeviceInstance(@RequestBody OrderDTO orderDTO){
        return deviceInstanceService.addDeviceInstance(orderDTO);
    }

    @GetMapping("/{instanceId}")
    public ResponseEntity<ResponseDTO> getVendorId(@PathVariable long instanceId){
        return deviceInstanceService.getVendorId(instanceId);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<String> updateDeviceInstance(@PathVariable("orderId") long orderId){
        return deviceInstanceService.updateDeviceInstance(orderId);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DeviceInstance>> getDeviceInstancesByCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(deviceInstanceService.getDeviceInstancesByCompany(companyId));
    }

    @PutMapping("{status}/{instanceId}")
    public ResponseEntity<DeviceInstance> updateDeviceStatus(@PathVariable String status,
                                                             @PathVariable long instanceId){
        return ResponseEntity.ok(deviceInstanceService.updateDeviceStatus(status,instanceId));
    }
}
