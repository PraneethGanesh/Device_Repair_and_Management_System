package com.example.vendor_service.Controller;

import com.example.vendor_service.DTO.AuthRequest;
import com.example.vendor_service.DTO.AuthResponse;
import com.example.vendor_service.DTO.DeviceDTO;
import com.example.vendor_service.DTO.VendorDTO;
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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createVendor(@RequestBody VendorDTO vendor) {
        AuthResponse savedVendor = vendorService.createVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVendor);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest){
        return ResponseEntity.ok(vendorService.login(authRequest));
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
