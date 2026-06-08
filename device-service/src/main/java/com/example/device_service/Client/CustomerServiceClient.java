package com.example.device_service.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/employees/{employeeId}/company/{companyId}/exists")
    ResponseEntity<Boolean> employeeBelongsToCompany(
            @PathVariable UUID employeeId,
            @PathVariable UUID companyId);
}
