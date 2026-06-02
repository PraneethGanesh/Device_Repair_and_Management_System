package com.example.repair_service.controller;

import com.example.repair_service.dto.CloseRepairDTO;
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
    @PostMapping("{userId}")
    public ResponseEntity<?> raiseRequest(@Valid @RequestBody RepairRequestDTO dto,@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repairService.raiseRequest(dto,userId));
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
    @PutMapping("/{id}/close")
    public ResponseEntity<RepairRequest> close(
            @PathVariable long id,
            @Valid @RequestBody CloseRepairDTO dto) {
        return ResponseEntity.ok(repairService.closeRequest(id, dto));
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
    @PutMapping("/{id}/progress")
    public ResponseEntity<RepairRequest> markInProgress(
            @PathVariable long id,
            @RequestParam long vendorId) {
        return ResponseEntity.ok(repairService.markInProgress(id, vendorId));
    }

    // Vendor marks COMPLETED
    @PutMapping("/{id}/complete")
    public ResponseEntity<RepairRequest> markCompleted(
            @PathVariable long id,
            @RequestParam long vendorId) {
        return ResponseEntity.ok(repairService.markCompleted(id, vendorId));
    }
}
