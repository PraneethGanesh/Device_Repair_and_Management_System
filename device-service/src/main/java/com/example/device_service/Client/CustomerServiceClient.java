package com.example.device_service.Client;

import com.example.device_service.DTO.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/employees/{employeeId}/company/{companyId}/exists")
    ResponseEntity<Boolean> employeeBelongsToCompany(
            @PathVariable("employeeId") UUID employeeId,
            @PathVariable("companyId") UUID companyId);

    @GetMapping("/api/companies/user/{userId}")
    ResponseEntity<CompanyResponse> getCompanyByUserId(@PathVariable("userId") String userId);
}

