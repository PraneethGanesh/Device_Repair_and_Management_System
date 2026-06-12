package com.example.customer_service.service;


import com.example.customer_service.dto.CompanyRequest;
import com.example.customer_service.dto.CompanyResponse;
import com.example.customer_service.dto.OrderRequest;
import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.entity.Company;
import com.example.customer_service.exception.DuplicateResourceException;
import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.repository.CompanyRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RestClient orderClient;

    public CompanyService(CompanyRepository companyRepository, @LoadBalanced RestClient.Builder orderClientBuilder) {
        this.companyRepository = companyRepository;
        this.orderClient = orderClientBuilder.baseUrl("http://order-service").build();
    }

    public CompanyResponse registerCompany(String userId,String username, CompanyRequest request) {
        if (companyRepository.existsByGstNumber(request.getGstNumber())) {
            throw new DuplicateResourceException("Company with GST number already exists: " + request.getGstNumber());
        }

        Company company = new Company(
                userId,
                request.getCompanyName(),
                username,
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

    public CompanyResponse getCompanyByUserId(String userId) {
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

    public ResponseEntity<String> placeOrder(String userId, OrderRequest orderRequest) {
        Company company=companyRepository.findByUserId(userId).orElseThrow(
                ()-> new RuntimeException("Company not found")
        );
        ResponseEntity<String> response=orderClient.post()
                .uri("/api/order/{companyId}",company.getId())
                .body(orderRequest)
                .retrieve()
                .toEntity(String.class);
        return response;
    }

    public CompanyResponse getMyAccount(String userId) {
        Company company=companyRepository.findByUserId(userId).orElseThrow(
                ()->new RuntimeException("Company Not found")
        );
        return CompanyResponse.from(company);
    }
}
