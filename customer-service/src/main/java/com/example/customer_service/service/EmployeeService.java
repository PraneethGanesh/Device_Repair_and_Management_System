package com.dms.customerservice.service;

import com.dms.customerservice.dto.request.EmployeeRequest;
import com.dms.customerservice.dto.response.EmployeeResponse;
import com.dms.customerservice.entity.Company;
import com.dms.customerservice.entity.Employee;
import com.dms.customerservice.entity.InviteStatus;
import com.dms.customerservice.exception.DuplicateResourceException;
import com.dms.customerservice.exception.ResourceNotFoundException;
import com.dms.customerservice.repository.CompanyRepository;
import com.dms.customerservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyRepository companyRepository) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
    }

    public EmployeeResponse inviteEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already invited with email: " + request.getEmail());
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        Employee employee = new Employee(
                request.getEmail(),
                request.getFullName(),
                request.getDepartment(),
                request.getDesignation(),
                company
        );

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.from(saved);
    }

    public EmployeeResponse getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
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
