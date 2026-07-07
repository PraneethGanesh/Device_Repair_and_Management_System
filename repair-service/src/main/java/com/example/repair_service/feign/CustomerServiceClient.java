package com.example.repair_service.feign;

import com.example.repair_service.dto.CompanyResponse;
import com.example.repair_service.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/employees/user/{id}")
    ResponseEntity<EmployeeDTO> getEmployeeByUserId(@PathVariable("id") String id);
    @GetMapping("/api/companies/user/{userId}")
    ResponseEntity<CompanyResponse> getCompanyByUserId(@PathVariable("userId") String userId);
}

