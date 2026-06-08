package com.example.device_service.Controller;

import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.DTO.DeviceResponseDTO;
import com.example.device_service.DTO.DeviceStatusDTO;
import com.example.device_service.DTO.OrderDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Service.DeviceService;
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
        return ResponseEntity.ok(deviceService.addDevice(deviceDTO,vendorId));
    }


    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable("id") long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Device> updateDevice(
//            @PathVariable long id,
//            @RequestBody DeviceDTO deviceDTO) {
//        return ResponseEntity.ok(deviceService.updateDevice(id, deviceDTO));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteDevice(@PathVariable long id) {
//        deviceService.deleteDevice(id);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByVendor(@PathVariable long vendorId){
        return ResponseEntity.ok(deviceService.getDeviceByVendor(vendorId));
    }


}
