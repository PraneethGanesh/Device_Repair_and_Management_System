package com.example.vendor_service.Repository;

import com.example.vendor_service.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor,Long> {
    Optional<Vendor> findByEmail(String name);
    Optional<Vendor> findByUserId(String userId);
    @Query(value = "select * from vendor where approval_status= :status",nativeQuery = true)
    List<Vendor> findByApproval(@Param("status") String status);
}
