package com.example.customer_service.controller;

import com.example.customer_service.dto.EmployeeDTO;
import com.example.customer_service.dto.EmployeeRequest;
import com.example.customer_service.dto.EmployeeResponse;
import com.example.customer_service.entity.InviteStatus;
import com.example.customer_service.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // POST /api/employees/invite — Invite an employee
    @PostMapping("/invite")
    public ResponseEntity<EmployeeResponse> inviteEmployee(@Valid @RequestBody EmployeeRequest request,
                                                           @RequestHeader("X-Auth-Id") String userId) {
        EmployeeResponse response = employeeService.inviteEmployee(request,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/employees/{id} — Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeByUserId(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeByUserId(id));
    }

    // GET /api/employees/company/{companyId} — Get all employees of a company
    @GetMapping("/company")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByCompanyId(@RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(employeeService.getEmployeesByCompanyId(userId));
    }

    // GET /api/employees/company/{companyId}/status/{status} — Filter by invite status
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(
            @PathVariable UUID companyId,
            @PathVariable InviteStatus status) {
        return ResponseEntity.ok(employeeService.getEmployeesByCompanyIdAndStatus(companyId, status));
    }

    // GET /api/employees/{employeeId}/company/{companyId}/exists — Check employee belongs to company
    @GetMapping("/{employeeId}/company/{companyId}/exists")
    public ResponseEntity<Boolean> employeeBelongsToCompany(
            @PathVariable UUID employeeId,
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(employeeService.employeeBelongsToCompany(employeeId, companyId));
    }

    // PATCH /api/employees/{id}/accept — Accept an invite
    @PatchMapping("/{id}/accept")
    public ResponseEntity<EmployeeResponse> acceptInvite(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.acceptInvite(id));
    }

    // PUT /api/employees/{id} — Update employee details
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // DELETE /api/employees/{id} — Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
