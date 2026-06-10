package com.example.audit_service.controller;

import com.example.audit_service.entity.AuditRecord;
import com.example.audit_service.repository.AuditRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditRecordRepository auditRecordRepository;

    public AuditController(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    @GetMapping("/repair/{repairId}")
    public List<AuditRecord> getByRepairId(@PathVariable Long repairId) {
        return auditRecordRepository.findByRepairIdOrderByTimestampDesc(repairId);
    }

    @GetMapping("/device/{deviceId}")
    public List<AuditRecord> getByDeviceId(@PathVariable Long deviceId) {
        return auditRecordRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @GetMapping("/vendor/{vendorId}")
    public List<AuditRecord> getByVendorId(@PathVariable Long vendorId) {
        return auditRecordRepository.findByVendorIdOrderByTimestampDesc(vendorId);
    }

    @GetMapping("/company/{companyId}")
    public List<AuditRecord> getByCompanyId(@PathVariable Long companyId) {
        return auditRecordRepository.findByCompanyIdOrderByTimestampDesc(companyId);
    }
}

