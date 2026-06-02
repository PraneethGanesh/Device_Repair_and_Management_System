package com.example.user_service.feign;

import com.example.user_service.dto.RepairRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "repair-service")
public interface RepairserviceClient {
    @PostMapping("/api/repairs/{userId}/{vendorId}")
    ResponseEntity<?> raiseRequest(@RequestBody RepairRequestDTO dto, @PathVariable long userId, @PathVariable long vendorId);

}
