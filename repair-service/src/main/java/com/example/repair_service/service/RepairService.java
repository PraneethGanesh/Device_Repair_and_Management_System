package com.example.repair_service.service;

import com.example.repair_service.dto.*;
import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RecipientRole;
import com.example.repair_service.enums.RepairStatus;
import com.example.repair_service.feign.DeviceServiceClient;
import com.example.repair_service.publisher.NotificationPublisher;
import com.example.repair_service.repository.RepairRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairService {

    private final RepairRequestRepository repairRepository;
    private final DeviceServiceClient deviceServiceClient;
    private final NotificationPublisher notificationPublisher;

    public RepairService(RepairRequestRepository repairRepository,
                         DeviceServiceClient deviceServiceClient,
                         NotificationPublisher notificationPublisher) {
        this.repairRepository = repairRepository;
        this.deviceServiceClient = deviceServiceClient;
        this.notificationPublisher = notificationPublisher;
    }

    // ─── 1. Employee/Admin raises a repair request ────────────────────────────
    public RepairRequest raiseRequest(RepairRequestDTO dto,long userId) {
        RepairRequest request = new RepairRequest();
        request.setDeviceId(dto.getDeviceId());
        request.setRaisedBy(userId);
        request.setIssueDescription(dto.getIssueDescription());
        request.setUrgent(dto.isUrgent());
        request.setStatus(RepairStatus.PENDING);

        RepairRequest saved = repairRepository.save(request);

        // Notify ADMIN — repair request raised
        notificationPublisher.publishRepairRaised(new NotificationDTO(
                userId,
                RecipientRole.EMPLOYEE,
                RecipientRole.ADMIN,
                "Repair request #" + saved.getRequestId() +
                " raised for device ID: " + dto.getDeviceId() +
                (dto.isUrgent() ? " [URGENT]" : "") +
                " | Issue: " + dto.getIssueDescription()
        ));

        return saved;
    }

    // ─── 2. Admin acknowledges and forwards to vendor pool ────────────────────
    public RepairRequest acknowledgeRequest(long requestId, long adminId) {
        RepairRequest request = getById(requestId);

        if (request.getStatus() != RepairStatus.PENDING) {
            throw new IllegalStateException(
                "Only PENDING requests can be acknowledged. Current status: " + request.getStatus());
        }

        request.setAdminId(adminId);
        request.setStatus(RepairStatus.ACKNOWLEDGED);
        RepairRequest saved = repairRepository.save(request);

        // Notify vendors — new repair request available (broadcast to VENDOR role)
        notificationPublisher.publishRepairAcknowledged(new NotificationDTO(
                adminId,
                RecipientRole.ADMIN,
                RecipientRole.VENDOR,
                "New repair request #" + requestId +
                " is available for pickup. Device ID: " + request.getDeviceId() +
                " | Issue: " + request.getIssueDescription()
        ));

        return saved;
    }

    // ─── 3. Vendor self-assigns a request ─────────────────────────────────────
    public RepairRequest assignVendor(long requestId, long vendorId) {
        RepairRequest request = getById(requestId);

        // Vendor can pick ACKNOWLEDGED or URGENT PENDING
        boolean canAssign = request.getStatus() == RepairStatus.ACKNOWLEDGED ||
                (request.getStatus() == RepairStatus.PENDING && request.isUrgent());

        if (!canAssign) {
            throw new IllegalStateException(
                "Request is not available for vendor assignment. Status: " + request.getStatus());
        }

        if (request.getVendorId() != null) {
            throw new IllegalStateException(
                "Request already assigned to vendor ID: " + request.getVendorId());
        }

        request.setVendorId(vendorId);
        request.setStatus(RepairStatus.ASSIGNED_TO_VENDOR);
        return repairRepository.save(request);
    }

    // ─── 4. Vendor starts work ────────────────────────────────────────────────
    public RepairRequest markInProgress(long requestId, long vendorId) {
        RepairRequest request = getById(requestId);
        validateVendorOwnership(request, vendorId);

        if (request.getStatus() != RepairStatus.ASSIGNED_TO_VENDOR) {
            throw new IllegalStateException(
                "Request must be ASSIGNED_TO_VENDOR before marking IN_PROGRESS.");
        }

        request.setStatus(RepairStatus.IN_PROGRESS);
        return repairRepository.save(request);
    }

    // ─── 5. Vendor marks repair complete ─────────────────────────────────────
    public RepairRequest markCompleted(long requestId, long vendorId) {
        RepairRequest request = getById(requestId);
        validateVendorOwnership(request, vendorId);

        if (request.getStatus() != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Request must be IN_PROGRESS before marking COMPLETED.");
        }

        request.setStatus(RepairStatus.COMPLETED);
        RepairRequest saved = repairRepository.save(request);

        // Notify ADMIN — vendor completed, admin needs to close and reassign device
        notificationPublisher.publishRepairCompleted(new NotificationDTO(
                vendorId,
                RecipientRole.VENDOR,
                RecipientRole.ADMIN,
                "Repair request #" + requestId +
                " has been COMPLETED by vendor ID: " + vendorId +
                ". Please close the ticket and reassign the device."
        ));

        return saved;
    }

    // ─── 6. Admin closes ticket and reassigns device ──────────────────────────
    public RepairRequest closeRequest(long requestId, CloseRepairDTO dto) {
        RepairRequest request = getById(requestId);

        if (request.getStatus() != RepairStatus.COMPLETED) {
            throw new IllegalStateException(
                "Only COMPLETED requests can be closed. Current status: " + request.getStatus());
        }

        request.setStatus(RepairStatus.CLOSED);
        RepairRequest saved = repairRepository.save(request);

        // Update device status to ASSIGNED and link to new/same employee
        deviceServiceClient.updateDeviceStatus(
                request.getDeviceId(),
                new DeviceStatusDTO("ASSIGNED")
        );

        // Notify EMPLOYEE — device is back and assigned to them
        notificationPublisher.publishRepairClosed(new NotificationDTO(
                dto.getAdminId(),
                RecipientRole.ADMIN,
                RecipientRole.EMPLOYEE,
                dto.getAssignToEmployeeId(),
                "Your device (ID: " + request.getDeviceId() +
                ") has been repaired and assigned back to you. Repair request #" + requestId + " is now CLOSED."
        ));

        return saved;
    }

    // ─── Queries ──────────────────────────────────────────────────────────────

    public RepairRequest getById(long requestId) {
        return repairRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Repair request not found: " + requestId));
    }

    public List<RepairRequest> getAllByEmployee(long employeeId) {
        return repairRepository.findByRaisedBy(employeeId);
    }

    public List<RepairRequest> getAllByVendor(long vendorId) {
        return repairRepository.findByVendorId(vendorId);
    }

    // Vendor sees ACKNOWLEDGED + URGENT PENDING
    public List<RepairRequest> getAvailableForVendor() {
        return repairRepository.findAvailableForVendor();
    }

    public List<RepairRequest> getAll() {
        return repairRepository.findAll();
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private void validateVendorOwnership(RepairRequest request, long vendorId) {
        if (request.getVendorId() == null || request.getVendorId() != vendorId) {
            throw new IllegalStateException(
                "Vendor ID: " + vendorId + " is not assigned to this request.");
        }
    }
}
