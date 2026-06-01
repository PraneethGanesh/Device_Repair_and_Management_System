package com.example.vendor_service.Controller;

import com.example.vendor_service.DTO.DeviceDTO;
import com.example.vendor_service.Entity.Vendor;
import com.example.vendor_service.Service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public ResponseEntity<Vendor> createVendor(@Valid @RequestBody Vendor vendor) {
        Vendor savedVendor = vendorService.createVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVendor);
    }

    @PostMapping("/devices")
    public ResponseEntity<DeviceDTO> addDevice(@RequestBody DeviceDTO deviceDTO) {
        return vendorService.addDevice(deviceDTO);
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<List<DeviceDTO>> getDevices(@PathVariable int vendorId){
       return ResponseEntity.ok(vendorService.getDevices(vendorId));
    }
}
