package com.example.repair_service.repository;

import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long> {

    // All requests raised by a specific employee/admin
    List<RepairRequest> findByRaisedBy(long raisedBy);

    // All requests assigned to a specific vendor
    List<RepairRequest> findByVendorId(long vendorId);

    // All requests by status
    List<RepairRequest> findByStatus(RepairStatus status);

    // Vendor sees: ACKNOWLEDGED requests OR (PENDING + urgent)
    @Query("SELECT r FROM RepairRequest r WHERE r.status = com.example.repair_service.enums.RepairStatus.ACKNOWLEDGED " +
           "OR (r.status = com.example.repair_service.enums.RepairStatus.PENDING AND r.urgent = true)")
    List<RepairRequest> findAvailableForVendor();

    @Query("""
       SELECT r
       FROM RepairRequest r
       WHERE r.status = :status
       AND r.vendorId = :vendorId
       """)
    List<RepairRequest> findByStatusAndVendorId(
            @Param("status") RepairStatus status,
            @Param("vendorId") long vendorId);
}
