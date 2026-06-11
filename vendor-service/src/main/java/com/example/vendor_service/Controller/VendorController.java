package com.example.vendor_service.Controller;

import com.example.vendor_service.DTO.*;
import com.example.vendor_service.Entity.Vendor;
import com.example.vendor_service.Service.VendorService;
import org.springframework.cloud.client.loadbalancer.Response;
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

    @GetMapping("/{userId}")
    public ResponseEntity<VendorDTO> getVendor(String userId){
        return  vendorService.getVendor(userId);
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

    @PutMapping("/review/{orderId}")
    public ResponseEntity<String> acceptOrders(@PathVariable long orderId,
                                               @RequestHeader("X-Auth-Id") String userId){
        return vendorService.acceptOrders(orderId,userId);
    }

    @GetMapping("/orders")
    public List<OrderDTO> getOrders(@RequestHeader("X-Auth-Id") String userId){
        return vendorService.getOrders(userId);
    }

    @GetMapping("/approvals")
    public ResponseEntity<?> getApprovalAccount(@RequestHeader("X-Auth-Role") String Role){
        return vendorService.getApprovalAccount(Role);
    }

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
