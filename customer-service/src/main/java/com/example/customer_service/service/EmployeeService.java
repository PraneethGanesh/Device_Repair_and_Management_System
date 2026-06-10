package com.example.customer_service.service;

import com.example.customer_service.client.UserServiceClient;
import com.example.customer_service.dto.*;
import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.entity.Company;
import com.example.customer_service.entity.Employee;
import com.example.customer_service.entity.InviteStatus;
import com.example.customer_service.exception.DuplicateResourceException;
import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.repository.CompanyRepository;
import com.example.customer_service.repository.EmployeeRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final UserServiceClient userServiceClient;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            CompanyRepository companyRepository,
            UserServiceClient userServiceClient) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.userServiceClient = userServiceClient;
    }

    public EmployeeResponse inviteEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already invited with email: " + request.getEmail());
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (company.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Company must be approved before employees can be registered");
        }

        UserRegistrationResponse user = userServiceClient.registerCompanyEmployee(
                new UserRegistrationRequest(request.getFullName(), request.getEmail(), request.getPassword())
        );

        Employee employee = new Employee(
                request.getEmail(),
                request.getFullName(),
                request.getDepartment(),
                request.getDesignation(),
                company
        );
        employee.setUserId(user.getId());
        employee.setInviteStatus(InviteStatus.ACCEPTED);

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.from(saved);
    }

    public EmployeeDTO getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return toEmployeeDTO(employee);
    }

    private EmployeeDTO toEmployeeDTO(Employee employee){
        EmployeeDTO employeeDTO=new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setCompanyId(employee.getCompany().getId());
        employeeDTO.setUserId(employee.getUserId());
        return employeeDTO;
    }

    public EmployeeResponse getEmployeeByUserId(String id) {
       Employee employee=employeeRepository.findByUserId(id).orElseThrow(
               () -> new ResourceNotFoundException("Employee not found with id: " + id)
       );
       return EmployeeResponse.from(employee);
    }

    public List<EmployeeResponse> getEmployeesByCompanyId(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        return employeeRepository.findByCompanyId(companyId)
                .stream()
                .map(EmployeeResponse::from)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getEmployeesByCompanyIdAndStatus(UUID companyId, InviteStatus status) {
        return employeeRepository.findByCompanyIdAndInviteStatus(companyId, status)
                .stream()
                .map(EmployeeResponse::from)
                .collect(Collectors.toList());
    }

    public boolean employeeBelongsToCompany(UUID employeeId, UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return employeeRepository.existsByIdAndCompanyId(employeeId, companyId);
    }

    public EmployeeResponse acceptInvite(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getInviteStatus() == InviteStatus.ACCEPTED) {
            throw new DuplicateResourceException("Invite already accepted for employee: " + employeeId);
        }

        employee.setInviteStatus(InviteStatus.ACCEPTED);
        Employee updated = employeeRepository.save(employee);
        return EmployeeResponse.from(updated);
    }

    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setFullName(request.getFullName());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());

        Employee updated = employeeRepository.save(employee);
        return EmployeeResponse.from(updated);
    }

    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
