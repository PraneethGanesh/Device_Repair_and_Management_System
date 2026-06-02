package com.example.user_service.service.impl;

import com.example.user_service.dto.*;
import com.example.user_service.entity.Employee;
import com.example.user_service.entity.Role;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.feign.DeviceServiceClient;
import com.example.user_service.feign.RepairserviceClient;
import com.example.user_service.repository.EmployeeRepository;
import com.example.user_service.security.JwtUtil;
import com.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final DeviceServiceClient deviceServiceClient;
    private final RepairserviceClient repairserviceClient;
    public UserServiceImpl(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           DeviceServiceClient deviceServiceClient, RepairserviceClient repairserviceClient) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.deviceServiceClient = deviceServiceClient;
        this.repairserviceClient = repairserviceClient;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role role = request.getRole() != null
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.EMPLOYEE;

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setDepartment(request.getDepartment());
        employee.setRole(role);

        Employee saved = employeeRepository.save(employee);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole().name());

        return buildAuthResponse(saved, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found for: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        String token = jwtUtil.generateToken(employee.getEmail(), employee.getRole().name());
        return buildAuthResponse(employee, token);
    }

    @Override
    public EmployeeResponse getMyProfile(String username,String role) {

        Employee emp = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + username));
        return toResponse(emp);
    }

    @Override
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse updateRole(Long id, String role) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        emp.setRole(Role.valueOf(role.toUpperCase()));
        return toResponse(employeeRepository.save(emp));
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public List<?> getAssignedDevices(String username) {
        Employee emp = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + username));
        return deviceServiceClient.getDevicesByEmployee(emp.getId());
    }

    @Override
    public ResponseEntity<?> assigndevices(AssignmentRequest assignmentRequest, String username) {
        Employee admin = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + username));
        Employee employee=employeeRepository.findById(assignmentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + assignmentRequest.getUserId()));

        if(!admin.getRole().equals(Role.ADMIN)){
            return ResponseEntity.badRequest().body("only admin can assign devices");
        }
        ResponseEntity<DeviceDTO> response=deviceServiceClient.assignDevice(assignmentRequest);
        DeviceDTO deviceDTO= response.getBody();
        return ResponseEntity.ok("Device:"+ deviceDTO.getDeviceName()+" is assigned to employee:"+employee.getEmail());
    }

    @Override
    public ResponseEntity<?> raiseRepairRequest(RepairRequestDTO repairRequestDTO, String username) {
        Employee employee=employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + username));
        DeviceResponseDTO deviceResponseDTO=deviceServiceClient.deviceOwner(repairRequestDTO.getDeviceId());
        if(deviceResponseDTO.getAssignedtoId()!=employee.getId()){
            return ResponseEntity.badRequest().body("Employee should own the device raise a request");
        }
        repairserviceClient.raiseRequest(repairRequestDTO,employee.getId(),deviceResponseDTO.getVendorId());
        return ResponseEntity.ok("rasied a repair request for device with id:"+repairRequestDTO.getDeviceId());
    }

    @Override
    public ResponseEntity<?> acknwoledgeRequest(String username, long id) {
        Employee employee=employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + username));
        if(!employee.getRole().equals(Role.ADMIN)){
            return ResponseEntity.badRequest().body("only admin can acknoledge the request");
        }
        repairserviceClient.acknowledge(id, employee.getId());
        return ResponseEntity.ok("Repairequest with id:"+id+" is acknowledged by admin:"+username);
    }

    @Override
    public ResponseEntity<?> closeRequest(String username, long id) {
        Employee employee=employeeRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + username));
        if(!employee.getRole().equals(Role.ADMIN)){
            return ResponseEntity.badRequest().body("only admin can close the request");
        }
        repairserviceClient.close(id);
        return ResponseEntity.ok("Repair request:"+id+"is closed");
    }

    // ── helpers ──────────────────────────────────────────────

    private AuthResponse buildAuthResponse(Employee emp, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(emp.getId());
        response.setName(emp.getName());
        response.setEmail(emp.getEmail());
        response.setRole(emp.getRole().name());
        response.setDepartment(emp.getDepartment());
        return response;
    }

    private EmployeeResponse toResponse(Employee emp) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(emp.getId());
        response.setName(emp.getName());
        response.setEmail(emp.getEmail());
        response.setDepartment(emp.getDepartment());
        response.setRole(emp.getRole().name());
        response.setJoinedDate(emp.getJoinedDate());
        return response;
    }
}