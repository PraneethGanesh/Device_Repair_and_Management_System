package com.example.user_service.feign;

import com.example.user_service.dto.RepairRequestDTO;
import com.example.user_service.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "repair-service")
public interface RepairserviceClient {
    @PostMapping("/api/repairs/{userId}/{vendorId}")
    ResponseEntity<?> raiseRequest(@RequestBody RepairRequestDTO dto, @PathVariable long userId, @PathVariable long vendorId);
    @PutMapping("/api/repairs/{id}/acknowledge")
    ResponseEntity<?> acknowledge( @PathVariable long id, @RequestParam long adminId);
    @PutMapping("/api/repairs/{repairId}/close")
    ResponseEntity<?> close(@PathVariable long repairId);

    @GetMapping("/api/repairs//employee/{employeeId}")
    ResponseEntity<List<ResponseDTO>> getByEmployee(@PathVariable long employeeId);

}
