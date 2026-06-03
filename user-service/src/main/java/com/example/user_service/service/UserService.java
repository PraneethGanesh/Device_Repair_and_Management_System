package com.example.user_service.service;


import com.example.user_service.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    EmployeeResponse getMyProfile(String username,String role);
    EmployeeResponse getById(Long id);
    List<EmployeeResponse> getAll();
    EmployeeResponse updateRole(Long id, String role);
    void deleteEmployee(Long id);
    List<?> getAssignedDevices(String username);
    List<?> getAssignedDevicesByEmployeeId(Long employeeId);

    ResponseEntity<?> assigndevices(AssignmentRequest assignmentRequest, String username);

    ResponseEntity<?> raiseRepairRequest(RepairRequestDTO repairRequestDTO,String username);

    ResponseEntity<?> acknwoledgeRequest(String username, long id);

    ResponseEntity<?> closeRequest(String username, long id);

    List<ResponseDTO> getRepairRequest(String username);
}
