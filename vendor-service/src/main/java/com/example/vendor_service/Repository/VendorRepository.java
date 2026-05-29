package com.example.vendor_service.Repository;

import com.example.vendor_service.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor,Long> {
}
