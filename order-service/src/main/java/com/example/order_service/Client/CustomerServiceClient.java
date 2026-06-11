package com.example.order_service.Client;

import com.example.order_service.DTO.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/companies/{id}")
    ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID id);
}
