package com.example.audit_service.repository;


import com.example.audit_service.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    List<AuditRecord> findByRepairIdOrderByTimestampDesc(Long repairId);

    List<AuditRecord> findByDeviceIdOrderByTimestampDesc(Long deviceId);

    List<AuditRecord> findByVendorIdOrderByTimestampDesc(Long vendorId);

    List<AuditRecord> findByCompanyIdOrderByTimestampDesc(Long companyId);
}

