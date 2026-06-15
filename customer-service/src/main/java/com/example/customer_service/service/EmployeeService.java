package com.example.customer_service.service;

import com.example.customer_service.client.UserServiceClient;
import com.example.customer_service.dto.*;
import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.entity.Company;
import com.example.customer_service.entity.Employee;
import com.example.customer_service.entity.InviteStatus;
import com.example.customer_service.exception.CompanyNotFoundException;
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

    public EmployeeResponse inviteEmployee(EmployeeRequest request,String userId) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already invited with email: " + request.getEmail());
        }

        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + userId));

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

    public EmployeeResponse getEmployeeOrCreate(String userId, String email, String role) {
        return employeeRepository.findByUserId(userId)
                .map(EmployeeResponse::from)
                .orElseGet(() -> {
                    if (role != null && role.equalsIgnoreCase("COMPANY_EMPLOYEE")) {
                        Company company = companyRepository.findAll().stream().findFirst().orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setUserId("system");
                            newCompany.setCompanyName("Default Company");
                            newCompany.setGstNumber("00AAAAA0000A0Z0");
                            newCompany.setAddress("Default Address");
                            newCompany.setApprovalStatus(ApprovalStatus.APPROVED);
                            return companyRepository.save(newCompany);
                        });

                        Employee employee = new Employee();
                        employee.setUserId(userId);
                        employee.setEmail(email != null ? email : "employee@company.com");

                        String derivedName = "Employee";
                        if (email != null && email.contains("@")) {
                            String firstPart = email.split("@")[0];
                            derivedName = java.util.Arrays.stream(firstPart.split("[._]"))
                                    .filter(s -> !s.isEmpty())
                                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                                    .collect(Collectors.joining(" "));
                        }
                        employee.setFullName(derivedName);
                        employee.setCompany(company);
                        employee.setInviteStatus(InviteStatus.ACCEPTED);
                        employee.setDepartment("Engineering");
                        employee.setDesignation("Software Engineer");

                        Employee saved = employeeRepository.save(employee);
                        return EmployeeResponse.from(saved);
                    }
                    throw new ResourceNotFoundException("Employee not found with user id: " + userId);
                });
    }

    public List<EmployeeResponse> getEmployeesByCompanyId(String userId) {
        Company company=companyRepository.findByUserId(userId).orElseThrow(
                ()->new CompanyNotFoundException("Company with Id:"+userId+" Not Found")
        );
        return employeeRepository.findByCompanyId(company.getId())
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
