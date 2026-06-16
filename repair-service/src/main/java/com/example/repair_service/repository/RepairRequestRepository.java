package com.example.repair_service.repository;

import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long> {

    List<RepairRequest> findByRaisedBy(UUID raisedBy);
//
//    // All requests assigned to a specific vendor
//    List<RepairRequest> findByVendorId(long vendorId);
//
//    // All requests by status
//    List<RepairRequest> findByStatus(RepairStatus status);

//    // Vendor sees: ACKNOWLEDGED requests OR (PENDING)
    @Query("SELECT r FROM RepairRequest r WHERE r.status = :acknowledged " +
            "OR (r.status = :pending)")
    List<RepairRequest> findAvailableForVendor(
            @Param("acknowledged") RepairStatus acknowledged,
            @Param("pending") RepairStatus pending
    );

    @Query("""
       SELECT r
       FROM RepairRequest r
       WHERE r.status = :status
       AND r.vendorId = :vendorId
       """)
    List<RepairRequest> findByStatusAndVendorId(
            @Param("status") RepairStatus status,
            @Param("vendorId") long vendorId);

    List<RepairRequest> findByCompanyIdAndStatus(UUID companyId,RepairStatus status);

    List<RepairRequest> findByCompanyId(UUID companyId);
}
