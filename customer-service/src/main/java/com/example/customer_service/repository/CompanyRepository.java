package com.dms.customerservice.repository;

import com.dms.customerservice.entity.ApprovalStatus;
import com.dms.customerservice.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByUserId(UUID userId);

    Optional<Company> findByGstNumber(String gstNumber);

    List<Company> findByApprovalStatus(ApprovalStatus approvalStatus);

    boolean existsByGstNumber(String gstNumber);
}
