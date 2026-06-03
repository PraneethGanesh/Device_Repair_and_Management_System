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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(vendorService.login(authRequest));
    }

    @PostMapping("/devices")
    public ResponseEntity<?> addDevice(@RequestBody DeviceDTO deviceDTO,
                                               @RequestHeader("X-Auth-User") String username,
                                               @RequestHeader("X-Auth-Role") String role) {
        return vendorService.addDevice(deviceDTO,username,role);
    }

    @GetMapping("/devices")
    public ResponseEntity<?> getDevices(@RequestHeader("X-Auth-User") String username,
                                                      @RequestHeader("X-Auth-Role") String role){
       return vendorService.getDevices(username,role);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyprofile(@RequestHeader("X-Auth-User") String username){
        return vendorService.getMyprofile(username);
    }


    @PutMapping("/mark/progress/{repairId}")
    public ResponseEntity<?> markInProgress(@RequestHeader("X-Auth-User") String username,
                                          @PathVariable long repairId){
        return vendorService.markInProgress(username,repairId);
    }

    @PutMapping("/mark/complete/{repairId}")
    public ResponseEntity<?> markCompleted(@RequestHeader("X-Auth-User") String username,
                                            @PathVariable long repairId){
        return vendorService.markCompleted(username,repairId);
    }
}
