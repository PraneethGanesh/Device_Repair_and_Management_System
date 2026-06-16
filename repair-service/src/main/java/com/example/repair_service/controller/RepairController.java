package com.example.repair_service.controller;

import com.example.repair_service.dto.RepairRequestDTO;
import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.service.RepairService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    // ─── EMPLOYEE / ADMIN ─────────────────────────────────────────────────────

    // Raise a new repair request
    @PostMapping
    public ResponseEntity<?> raiseRequest(@Valid @RequestBody RepairRequestDTO dto,
                                          @RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repairService.raiseRequest(dto,userId));
    }

    // acknowledge repair request by the company admin
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<RepairRequest> acknowledge(
            @PathVariable long id,
            @RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(repairService.acknowledgeRequest(id,userId));
    }

    @GetMapping("/open")
    public List<RepairRequest> getOpenRequestsByCompany(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getOpenRequestsByCompany(userId);
    }

    @GetMapping("/my")
    public List<RepairRequest> getMyRequests(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getRequestsByEmployee(userId);
    }

    @GetMapping("/company")
    public List<RepairRequest> getCompanyRequests(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getAllRequestsForCompany(userId);
    }

    @GetMapping
    public List<RepairRequest> getAllRequests() {
        return repairService.getAllRequests();
    }

     // Vendor marks repair as IN_PROGRESS
    @PutMapping("{id}/progress")
    public ResponseEntity<RepairRequest> markInProgress(@PathVariable long id,
                                                        @RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(repairService.markInProgress(id, userId));
    }

    @GetMapping("/vendor")
    public List<RepairRequest> getRequestsAssignedToVendor(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getRequestsAssignedToVendor(userId);
    }

    // Vendor marks repair as repair_done
    @PutMapping("{id}/complete")
    public ResponseEntity<RepairRequest> markCompleted(@PathVariable long id,
                                                       @RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(repairService.markCompleted(id,userId));
    }
    @GetMapping("/vendor/inProgress")
    public List<RepairRequest> getRequestsInProgress(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getRequestsInProgress(userId);
    }

    // company admin closes repair
    @PutMapping("/{repairId}/close")
    public ResponseEntity<RepairRequest> close(
            @PathVariable long repairId, @RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(repairService.closeRequest(repairId,userId));
    }

     @GetMapping("/repaired")
    public List<RepairRequest> getRequestsRepaired(@RequestHeader("X-Auth-Id") String userId) {
        return repairService.getRequestsRepaired(userId);
    }





//    // Get repair request by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<RepairRequest> getById(@PathVariable long id) {
//        return ResponseEntity.ok(repairService.getById(id));
//    }
//
//    // Get all requests raised by a specific employee/admin
//    @GetMapping("/employee/{employeeId}")
//    public ResponseEntity<List<ResponseDTO>> getByEmployee(@PathVariable long employeeId) {
//        return ResponseEntity.ok(repairService.getAllByEmployee(employeeId));
//    }
//
//    // ─── ADMIN ────────────────────────────────────────────────────────────────
//
//    // Get all repair requests (admin view)
//    @GetMapping
//    public ResponseEntity<List<RepairRequest>> getAll() {
//        return ResponseEntity.ok(repairService.getAll());
//    }
//
//    // Admin acknowledges and forwards to vendor pool
//
//
//    // Admin closes ticket and reassigns device
//
//
//    // ─── VENDOR ───────────────────────────────────────────────────────────────
//
//    // Vendor sees available requests (ACKNOWLEDGED + URGENT PENDING)
//    @GetMapping("/available")
//    public ResponseEntity<List<RepairRequest>> getAvailableForVendor() {
//        return ResponseEntity.ok(repairService.getAvailableForVendor());
//    }
//
//    // Get all requests assigned to a specific vendor
//    @GetMapping("/vendor/{vendorId}")
//    public ResponseEntity<List<RepairRequest>> getByVendor(@PathVariable long vendorId) {
//        return ResponseEntity.ok(repairService.getAllByVendor(vendorId));
//    }
//
//    // Vendor self-assigns a request
//    @PutMapping("/{id}/assign-vendor")
//    public ResponseEntity<RepairRequest> assignVendor(
//            @PathVariable long id,
//            @RequestParam long vendorId) {
//        return ResponseEntity.ok(repairService.assignVendor(id, vendorId));
//    }
//
//
//
//    // Vendor marks COMPLETED
//
//
//
//    @GetMapping("/acknowledge/{vendorId}")
//    public ResponseEntity<List<ResponseDTO>> getAcknowledgedRepairRequestByVendor(@PathVariable long vendorId){
//        return ResponseEntity.ok(repairService.getAcknowledgedRepairRequestByVendor(vendorId));
//    }


}
