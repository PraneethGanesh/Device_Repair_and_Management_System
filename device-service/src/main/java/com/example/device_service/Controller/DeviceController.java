package com.example.device_service.Controller;

import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.DTO.DeviceResponseDTO;
import com.example.device_service.DTO.DeviceStatusDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/{vendorId}")
    public ResponseEntity<DeviceDTO> addDevice(@RequestBody DeviceDTO deviceDTO,
                                               @PathVariable long vendorId) {
        Device savedDevice = deviceService.addDevice(deviceDTO,vendorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ToDeviceDTO(savedDevice));
    }

    private DeviceDTO ToDeviceDTO(Device device){
        DeviceDTO deviceDTO=new DeviceDTO();
        deviceDTO.setDeviceName(device.getDeviceName());
        deviceDTO.setDeviceType(device.getDeviceType());
        deviceDTO.setWarrantyExpiry(device.getWarrantyExpiry());
        return deviceDTO;
    }

    @PostMapping("/assign")
    public ResponseEntity<DeviceDTO> assignDevice(@RequestBody AssignmentRequest assignmentRequest) {
        DeviceDTO assignedDevice = deviceService.assignDevice(assignmentRequest);
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

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByVendor(@PathVariable long vendorId){
        return ResponseEntity.ok(deviceService.getDeviceByVendor(vendorId));
    }

    @PutMapping("/status")
    public ResponseEntity<Device> updateDeviceStatus(
            @RequestBody DeviceStatusDTO statusDTO) {
        return ResponseEntity.ok(deviceService.updateDeviceStatus(statusDTO));
    }
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByEmployee(@PathVariable long employeeId) {
        return ResponseEntity.ok(deviceService.getDevicesByEmployee(employeeId));
    }

    @GetMapping("/owner/{id}")
    public DeviceResponseDTO deviceOwner(@PathVariable long id){
        Device device= deviceService.getDeviceById(id);
        DeviceResponseDTO deviceResponseDTO=new DeviceResponseDTO();
        deviceResponseDTO.setVendorId(device.getVendorId());
        deviceResponseDTO.setAssignedtoId(device.getAssignedToId());
        return deviceResponseDTO;
    }
}
