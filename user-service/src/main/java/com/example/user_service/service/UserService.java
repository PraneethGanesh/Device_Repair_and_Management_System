package com.example.user_service.service;


import com.example.user_service.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    EmployeeResponse getMyProfile(String username,String role);
    List<EmployeeResponse> getAll();
    EmployeeResponse updateRole(Long id, String role);
    void deleteEmployee(Long id);
    List<?> getAssignedDevices(String username);

    ResponseEntity<?> assigndevices(AssignmentRequest assignmentRequest, String username);

    ResponseEntity<?> raiseRepairRequest(RepairRequestDTO repairRequestDTO,String username);
}