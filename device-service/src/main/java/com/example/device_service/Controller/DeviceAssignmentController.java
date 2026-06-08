package com.example.device_service.Controller;

import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.Entity.DeviceAssignment;
import com.example.device_service.Service.DeviceAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
public class DeviceAssignmentController {
    private final DeviceAssignmentService deviceAssignmentService;

    public DeviceAssignmentController(DeviceAssignmentService deviceAssignmentService) {
        this.deviceAssignmentService = deviceAssignmentService;
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<DeviceAssignment> assignDevice(
            @PathVariable UUID companyId,
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(deviceAssignmentService.assignDevice(companyId, request));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DeviceAssignment>> getAssignmentsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(deviceAssignmentService.getAssignmentsByEmployee(employeeId));
    }

    @PutMapping("/{assignmentId}/return")
    public ResponseEntity<DeviceAssignment> returnDevice(@PathVariable long assignmentId) {
        return ResponseEntity.ok(deviceAssignmentService.returnDevice(assignmentId));
    }
}
