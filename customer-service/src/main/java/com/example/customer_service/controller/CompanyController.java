package com.dms.customerservice.controller;

import com.dms.customerservice.dto.request.CompanyRequest;
import com.dms.customerservice.dto.response.CompanyResponse;
import com.dms.customerservice.entity.ApprovalStatus;
import com.dms.customerservice.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // POST /api/companies — Register a company
    @PostMapping
    public ResponseEntity<CompanyResponse> registerCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/companies — Get all companies
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    // GET /api/companies/{id} — Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // GET /api/companies/user/{userId} — Get company by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<CompanyResponse> getCompanyByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(companyService.getCompanyByUserId(userId));
    }

    // GET /api/companies/status/{status} — Filter by approval status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CompanyResponse>> getCompaniesByStatus(@PathVariable ApprovalStatus status) {
        return ResponseEntity.ok(companyService.getCompaniesByStatus(status));
    }

    // PATCH /api/companies/{id}/status — Update approval status (admin)
    @PatchMapping("/{id}/status")
    public ResponseEntity<CompanyResponse> updateApprovalStatus(
            @PathVariable UUID id,
            @RequestParam ApprovalStatus status) {
        return ResponseEntity.ok(companyService.updateApprovalStatus(id, status));
    }

    // PUT /api/companies/{id} — Update company details
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    // DELETE /api/companies/{id} — Delete company
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
