package com.example.user_service.service;


import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.EmployeeResponse;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    EmployeeResponse getById(Long id);
    List<EmployeeResponse> getAll();
    EmployeeResponse updateRole(Long id, String role);
    void deleteEmployee(Long id);
    List<?> getAssignedDevices(Long employeeId);
}