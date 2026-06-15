package com.example.customer_service.controller;


import com.example.customer_service.dto.CompanyRequest;
import com.example.customer_service.dto.CompanyResponse;
import com.example.customer_service.dto.OrderDTO;
import com.example.customer_service.dto.OrderRequest;
import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.service.CompanyService;
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
    // CompanyController — correct way
    @PostMapping
    public ResponseEntity<CompanyResponse> registerCompany(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Auth-User") String username,
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.registerCompany(userId,username, request));
    }

    // GET /api/companies — Get all companies
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyResponse> getMyAccount(@RequestHeader("X-User-Id") String userId){
       return ResponseEntity.ok(companyService.getMyAccount(userId));
    }

    @GetMapping("/status")
    public boolean isApproved(@RequestHeader("X-User-Id") String userId){
        return companyService.isApproved(userId);
    }

    // GET /api/companies/{id} — Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // GET /api/companies/user/{userId} — Get company by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<CompanyResponse> getCompanyByUserId(@PathVariable String userId) {
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

    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(@RequestHeader("X-Auth-Id") String userId, @RequestBody OrderRequest orderRequest){
        return companyService.placeOrder(userId,orderRequest);
    }

    @GetMapping("/orders/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@RequestHeader("X-Auth-Id") String userId) {
        return ResponseEntity.ok(companyService.getMyOrders(userId));
    }

}
