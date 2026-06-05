package com.dms.customerservice.service;

import com.dms.customerservice.dto.request.CompanyRequest;
import com.dms.customerservice.dto.response.CompanyResponse;
import com.dms.customerservice.entity.ApprovalStatus;
import com.dms.customerservice.entity.Company;
import com.dms.customerservice.exception.DuplicateResourceException;
import com.dms.customerservice.exception.ResourceNotFoundException;
import com.dms.customerservice.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyResponse registerCompany(CompanyRequest request) {
        if (companyRepository.existsByGstNumber(request.getGstNumber())) {
            throw new DuplicateResourceException("Company with GST number already exists: " + request.getGstNumber());
        }

        Company company = new Company(
                request.getUserId(),
                request.getCompanyName(),
                request.getGstNumber(),
                request.getAddress()
        );

        Company saved = companyRepository.save(company);
        return CompanyResponse.from(saved);
    }

    public CompanyResponse getCompanyById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return CompanyResponse.from(company);
    }

    public CompanyResponse getCompanyByUserId(UUID userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for userId: " + userId));
        return CompanyResponse.from(company);
    }

    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(CompanyResponse::from)
                .collect(Collectors.toList());
    }

    public List<CompanyResponse> getCompaniesByStatus(ApprovalStatus status) {
        return companyRepository.findByApprovalStatus(status)
                .stream()
                .map(CompanyResponse::from)
                .collect(Collectors.toList());
    }

    public CompanyResponse updateApprovalStatus(UUID id, ApprovalStatus status) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        company.setApprovalStatus(status);
        Company updated = companyRepository.save(company);
        return CompanyResponse.from(updated);
    }

    public CompanyResponse updateCompany(UUID id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        company.setCompanyName(request.getCompanyName());
        company.setAddress(request.getAddress());
        // GST number is generally not changed — skipping update intentionally

        Company updated = companyRepository.save(company);
        return CompanyResponse.from(updated);
    }

    public void deleteCompany(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }
}
