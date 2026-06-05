package com.example.customer_service.repository;

import com.example.customer_service.entity.ApprovalStatus;
import com.example.customer_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByUserId(String userId);

    Optional<Company> findByGstNumber(String gstNumber);

    List<Company> findByApprovalStatus(ApprovalStatus approvalStatus);

    boolean existsByGstNumber(String gstNumber);
}
