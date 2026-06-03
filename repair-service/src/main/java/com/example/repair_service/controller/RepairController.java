package com.example.repair_service.controller;

import com.example.repair_service.dto.CloseRepairDTO;
import com.example.repair_service.dto.RepairRequestDTO;
import com.example.repair_service.dto.ResponseDTO;
import com.example.repair_service.dto.UpdateRepairStatusRequest;
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
    @PostMapping("/{userId}/{vendorId}")
    public ResponseEntity<?> raiseRequest(@Valid @RequestBody RepairRequestDTO dto,
                                          @PathVariable long userId,
                                          @PathVariable long vendorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repairService.raiseRequest(dto,userId,vendorId));
    }

    // Get repair request by ID
    @GetMapping("/{id}")
    public ResponseEntity<RepairRequest> getById(@PathVariable long id) {
        return ResponseEntity.ok(repairService.getById(id));
    }

    // Get all requests raised by a specific employee/admin
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<RepairRequest>> getByEmployee(@PathVariable long employeeId) {
        return ResponseEntity.ok(repairService.getAllByEmployee(employeeId));
    }

    // ─── ADMIN ────────────────────────────────────────────────────────────────

    // Get all repair requests (admin view)
    @GetMapping
    public ResponseEntity<List<RepairRequest>> getAll() {
        return ResponseEntity.ok(repairService.getAll());
    }

    // Admin acknowledges and forwards to vendor pool
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<RepairRequest> acknowledge(
            @PathVariable long id,
            @RequestParam long adminId) {
        return ResponseEntity.ok(repairService.acknowledgeRequest(id, adminId));
    }

    // Admin closes ticket and reassigns device
    @PutMapping("/{repairId}/close")
    public ResponseEntity<RepairRequest> close(
            @PathVariable long repairId) {
        return ResponseEntity.ok(repairService.closeRequest(repairId));
    }

    // ─── VENDOR ───────────────────────────────────────────────────────────────

    // Vendor sees available requests (ACKNOWLEDGED + URGENT PENDING)
    @GetMapping("/available")
    public ResponseEntity<List<RepairRequest>> getAvailableForVendor() {
        return ResponseEntity.ok(repairService.getAvailableForVendor());
    }

    // Get all requests assigned to a specific vendor
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<RepairRequest>> getByVendor(@PathVariable long vendorId) {
        return ResponseEntity.ok(repairService.getAllByVendor(vendorId));
    }

    // Vendor self-assigns a request
    @PutMapping("/{id}/assign-vendor")
    public ResponseEntity<RepairRequest> assignVendor(
            @PathVariable long id,
            @RequestParam long vendorId) {
        return ResponseEntity.ok(repairService.assignVendor(id, vendorId));
    }

    // Vendor marks IN_PROGRESS
    @PutMapping("/progress")
    public ResponseEntity<RepairRequest> markInProgress(
            @RequestBody UpdateRepairStatusRequest updateRepairStatusRequest) {
        return ResponseEntity.ok(repairService.markInProgress(updateRepairStatusRequest));
    }

    // Vendor marks COMPLETED
    @PutMapping("/complete")
    public ResponseEntity<RepairRequest> markCompleted(
            @RequestBody UpdateRepairStatusRequest updateRepairStatusRequest) {
        return ResponseEntity.ok(repairService.markCompleted(updateRepairStatusRequest));
    }


    @GetMapping("/acknowledge/{vendorId}")
    public ResponseEntity<List<ResponseDTO>> getAcknowledgedRepairRequestByVendor(@PathVariable long vendorId){
        return ResponseEntity.ok(repairService.getAcknowledgedRepairRequestByVendor(vendorId));
    }


}
