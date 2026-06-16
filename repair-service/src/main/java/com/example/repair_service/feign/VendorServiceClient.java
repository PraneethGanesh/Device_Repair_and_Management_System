package com.example.repair_service.feign;

import com.example.repair_service.dto.VendorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vendor-service")
public interface VendorServiceClient {
    @GetMapping("/api/vendors/{userId}")
    ResponseEntity<VendorDTO> getVendor(@PathVariable String userId);
}
