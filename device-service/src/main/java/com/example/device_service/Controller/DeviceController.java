package com.example.device_service.Controller;

import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> addDevice(@RequestBody DeviceDTO deviceDTO) {
        Device savedDevice = deviceService.addDevice(deviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ToDeviceDTO(savedDevice));

    }

    private DeviceDTO ToDeviceDTO(Device device){
        DeviceDTO deviceDTO=new DeviceDTO();
        deviceDTO.setDeviceName(device.getDeviceName());
        deviceDTO.setDeviceType(device.getDeviceType());
        deviceDTO.setVendorId(device.getVendorId());
        deviceDTO.setWarrantyExpiry(device.getWarrantyExpiry());
        return deviceDTO;
    }

    @PostMapping("/assign")
    public ResponseEntity<Device> assignDevice(@RequestBody AssignmentRequest assignmentRequest) {
        Device assignedDevice = deviceService.assignDevice(assignmentRequest);
        return ResponseEntity.ok(assignedDevice);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(
            @PathVariable long id,
            @RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(deviceService.updateDevice(id, deviceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
