package com.example.vendor_service.Controller;

import com.example.vendor_service.DTO.ActionDTO;
import com.example.vendor_service.DTO.DeviceDTO;
import com.example.vendor_service.DTO.RegisterDTO;
import com.example.vendor_service.Entity.Vendor;
import com.example.vendor_service.Service.VendorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping("/register")
    public ResponseEntity<Vendor> registerVendor(@RequestBody RegisterDTO registerDTO,
                                                 @RequestHeader("X-Auth-User") String username,
                                                 @RequestHeader("X-Auth-Id") String userId){
         return ResponseEntity.ok(vendorService.registerVendor(registerDTO,username,userId));
    }

    @PutMapping("/review")
    public ResponseEntity<?> approveVendor(@RequestBody ActionDTO actionDTO,
                                           @RequestHeader("X-Auth-Role") String role){
        return vendorService.approveVendor(actionDTO,role);
    }

    @GetMapping("/me")
    public ResponseEntity<?> myAccount(@RequestHeader("X-Auth-Id") String userId){
        return vendorService.myAccount(userId);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAccount(@RequestHeader("X-Auth-Role") String role){
        return vendorService.getPendingAccount(role);
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
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getMyprofile(@RequestHeader("X-Auth-User") String username){
//        return vendorService.getMyprofile(username);
//    }
//
//
//    @PutMapping("/mark/progress/{repairId}")
//    public ResponseEntity<?> markInProgress(@RequestHeader("X-Auth-User") String username,
//                                          @PathVariable long repairId){
//        return vendorService.markInProgress(username,repairId);
//    }
//
//    @PutMapping("/mark/complete/{repairId}")
//    public ResponseEntity<?> markCompleted(@RequestHeader("X-Auth-User") String username,
//                                            @PathVariable long repairId){
//        return vendorService.markCompleted(username,repairId);
//    }
}
