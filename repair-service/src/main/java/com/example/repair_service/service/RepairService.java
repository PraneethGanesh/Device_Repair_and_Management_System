package com.example.repair_service.service;

import com.example.repair_service.dto.*;
import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RecipientRole;
import com.example.repair_service.enums.RepairStatus;
import com.example.repair_service.feign.CustomerServiceClient;
import com.example.repair_service.feign.DeviceServiceClient;
import com.example.repair_service.kafka.RepairEventProducer;
import com.example.repair_service.publisher.NotificationPublisher;
import com.example.repair_service.repository.RepairRequestRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class RepairService {

    private final RepairRequestRepository repairRepository;
    private final DeviceServiceClient deviceServiceClient;
    private final NotificationPublisher notificationPublisher;
    private final RepairEventProducer repairEventProducer;
    private final CustomerServiceClient customerServiceClient;

    public RepairService(RepairRequestRepository repairRepository,
                         DeviceServiceClient deviceServiceClient,
                         NotificationPublisher notificationPublisher,
                         RepairEventProducer repairEventProducer, CustomerServiceClient customerServiceClient) {
        this.repairRepository = repairRepository;
        this.deviceServiceClient = deviceServiceClient;
        this.notificationPublisher = notificationPublisher;
        this.repairEventProducer = repairEventProducer;
        this.customerServiceClient = customerServiceClient;
    }

//    // ─── 1. Employee/Admin raises a repair request ────────────────────────────
    public RepairRequest raiseRequest(RepairRequestDTO dto,String userId) {
        EmployeeDTO response=customerServiceClient.getEmployeeByUserId(userId).getBody();
        UUID employeeId=deviceServiceClient.getDeviceAssignment(dto.getDeviceInstanceId()).getBody();
        if(employeeId!=response.getId()){
            throw new RuntimeException("Device should be assigned to u to raise a repair request");
        }
        ResponseDTO responseDTO=deviceServiceClient.getVendorId(dto.getDeviceInstanceId()).getBody();
        RepairRequest request = new RepairRequest();
        request.setDeviceInstanceId(dto.getDeviceInstanceId());
        request.setIssueDescription(dto.getIssueDescription());
        request.setRaisedBy(response.getId());
        request.setCompanyId(response.getCompanyId());
        request.setVendorId(responseDTO.getVendorId());
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RepairStatus.RAISED);

//        publishRepairEvent(
//                "REPAIR_CREATED",
//                saved,
//                null,
//                RepairStatus.PENDING,
//                false
//        );


        return repairRepository.save(request);
    }


//
//    // ─── 2. Admin acknowledges and forwards to vendor pool ────────────────────
    public RepairRequest acknowledgeRequest(long requestId, long adminId) {
        RepairRequest request = getById(requestId);

        if (request.getStatus() != RepairStatus.RAISED) {
            throw new IllegalStateException(
                    "Only PENDING requests can be acknowledged. Current status: " + request.getStatus());
        }
        RepairStatus previousStatus = request.getStatus();

        request.setStatus(RepairStatus.ASSIGNED_TO_VENDOR);
        deviceServiceClient.updateDeviceStatus("SENT_TO_REPAIR",request.getDeviceInstanceId());
        RepairRequest saved = repairRepository.save(request);

//        publishRepairEvent(
//                "REPAIR_ACKNOWLEDGED",
//                saved,
//                previousStatus,
//                RepairStatus.ASSIGNED_TO_VENDOR,
//                true
//        );
        // Notify vendors — new repair request available (broadcast to VENDOR role)
//        notificationPublisher.publishRepairAcknowledged(new NotificationDTO(
//                adminId,
//                RecipientRole.ADMIN,
//                RecipientRole.VENDOR,
//                "New repair request #" + requestId +
//                        " is available for pickup. Device ID: " + request.getDeviceId() +
//                        " | Issue: " + request.getIssueDescription()
//        ));

        return saved;
    }

    public RepairRequest getById(long requestId) {
        return repairRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Repair request not found: " + requestId));
    }
//
//    // ─── 3. Vendor self-assigns a request ─────────────────────────────────────
//    public RepairRequest assignVendor(long requestId, long vendorId) {
//        RepairRequest request = getById(requestId);
//
//        // Vendor can pick ACKNOWLEDGED or URGENT PENDING
//        boolean canAssign = request.getStatus() == RepairStatus.ACKNOWLEDGED ||
//                (request.getStatus() == RepairStatus.PENDING && request.isUrgent());
//
//        if (!canAssign) {
//            throw new IllegalStateException(
//                    "Request is not available for vendor assignment. Status: " + request.getStatus());
//        }
//
//        if (request.getVendorId() != null) {
//            throw new IllegalStateException(
//                    "Request already assigned to vendor ID: " + request.getVendorId());
//        }
//
//        request.setVendorId(vendorId);
//        request.setStatus(RepairStatus.ASSIGNED_TO_VENDOR);
//        return repairRepository.save(request);
//    }
//
//    // ─── 4. Vendor starts work ────────────────────────────────────────────────
//    public RepairRequest markInProgress(UpdateRepairStatusRequest updateRepairStatusRequest) {
//        RepairRequest request = getById(updateRepairStatusRequest.getRepairId());
//        validateVendorOwnership(request, updateRepairStatusRequest.getVendorId());
//
//        if (request.getStatus() != RepairStatus.ASSIGNED_TO_VENDOR) {
//            throw new IllegalStateException(
//                    "Request must be ASSIGNED_TO_VENDOR before marking IN_PROGRESS.");
//        }
//
//        DeviceStatusDTO deviceStatusDTO=new DeviceStatusDTO();
//        deviceStatusDTO.setStatus("UNDER_REPAIR");
//        deviceStatusDTO.setDeviceId(request.getDeviceId());
//        deviceServiceClient.updateDeviceStatus(deviceStatusDTO);
//        request.setStatus(RepairStatus.IN_PROGRESS);
//        RepairStatus previousStatus = request.getStatus();
//        publishRepairEvent(
//                "REPAIR_IN_PROGRESS",
//                request,
//                previousStatus,
//                RepairStatus.IN_PROGRESS,
//                false
//        );
//        return repairRepository.save(request);
//    }
//
//    // ─── 5. Vendor marks repair complete ─────────────────────────────────────
//    public RepairRequest markCompleted(UpdateRepairStatusRequest updateRepairStatusRequest) {
//        RepairRequest request = getById(updateRepairStatusRequest.getRepairId());
//        validateVendorOwnership(request, updateRepairStatusRequest.getVendorId());
//
//        if (request.getStatus() != RepairStatus.IN_PROGRESS) {
//            throw new IllegalStateException(
//                    "Request must be IN_PROGRESS before marking COMPLETED.");
//        }
//
//        request.setStatus(RepairStatus.COMPLETED);
//        RepairRequest saved = repairRepository.save(request);
//
//        DeviceStatusDTO deviceStatusDTO=new DeviceStatusDTO();
//        deviceStatusDTO.setStatus("REPAIR_DONE");
//        deviceStatusDTO.setDeviceId(request.getDeviceId());
//        deviceServiceClient.updateDeviceStatus(deviceStatusDTO);
//
//        // Notify ADMIN — vendor completed, admin needs to close and reassign device
//        notificationPublisher.publishRepairCompleted(new NotificationDTO(
//                updateRepairStatusRequest.getVendorId(),
//                RecipientRole.VENDOR,
//                RecipientRole.ADMIN,
//                "Repair request #" + updateRepairStatusRequest.getRepairId() +
//                        " has been COMPLETED by vendor ID: " + updateRepairStatusRequest.getVendorId() +
//                        ". Please close the ticket and reassign the device."
//        ));
//
//        RepairStatus previousStatus = request.getStatus();
//        publishRepairEvent(
//                "REPAIR_COMPLETED",
//                saved,
//                previousStatus,
//                RepairStatus.COMPLETED,
//                false
//        );
//
//        return saved;
//    }
//
//    // ─── 6. Admin closes ticket and reassigns device ──────────────────────────
//    public RepairRequest closeRequest(long repairId) {
//        RepairRequest request = getById(repairId);
//
//        if (request.getStatus() != RepairStatus.COMPLETED) {
//            throw new IllegalStateException(
//                    "Only COMPLETED requests can be closed. Current status: " + request.getStatus());
//        }
//
//        DeviceStatusDTO deviceStatusDTO=new DeviceStatusDTO();
//        deviceStatusDTO.setStatus("ASSIGNED");
//        deviceStatusDTO.setDeviceId(request.getDeviceId());
//        deviceServiceClient.updateDeviceStatus(deviceStatusDTO);
//
//        request.setStatus(RepairStatus.CLOSED);
//        RepairRequest saved = repairRepository.save(request);
//
//        notificationPublisher.publishRepairClosed(new NotificationDTO(
//                request.getAdminId(),
//                RecipientRole.ADMIN,
//                RecipientRole.EMPLOYEE,
//                request.getRaisedBy(),
//                "Your device (ID: " + request.getDeviceId()
//                        + ") has been repaired and assigned back to you. Repair request #"
//                        + request.getRequestId() + " is now CLOSED."
//        ));
//
//        RepairStatus previousStatus = request.getStatus();
//        publishRepairEvent(
//                "DEVICE_RETURNED",
//                saved,
//                previousStatus,
//                RepairStatus.CLOSED,
//                false
//        );
//
//        return saved;
//    }
//
//    // ─── Queries ──────────────────────────────────────────────────────────────
//
//    public RepairRequest getById(long requestId) {
//        return repairRepository.findById(requestId)
//                .orElseThrow(() -> new NoSuchElementException("Repair request not found: " + requestId));
//    }
//
//    public List<ResponseDTO> getAllByEmployee(long employeeId) {
//        List<RepairRequest> repairRequests=repairRepository.findByRaisedBy(employeeId);
//        return repairRequests.stream()
//                .map(request -> toResponseDTO(request))
//                .toList();
//    }
//
//    public List<RepairRequest> getAllByVendor(long vendorId) {
//        return repairRepository.findByVendorId(vendorId);
//    }
//
//    // Vendor sees ACKNOWLEDGED + URGENT PENDING
//    public List<RepairRequest> getAvailableForVendor() {
//        return repairRepository.findAvailableForVendor();
//    }
//
//    public List<RepairRequest> getAll() {
//        return repairRepository.findAll();
//    }
//
//    // ─── Helper ───────────────────────────────────────────────────────────────
//
//    private void validateVendorOwnership(RepairRequest request, long vendorId) {
//        if (request.getVendorId() == null || request.getVendorId() != vendorId) {
//            throw new IllegalStateException(
//                    "Vendor ID: " + vendorId + " is not assigned to this request.");
//        }
//    }
//
//    public List<ResponseDTO> getAcknowledgedRepairRequestByVendor(long vendorId) {
//        List<RepairRequest> repairRequests= repairRepository.findByStatusAndVendorId(RepairStatus.ACKNOWLEDGED,vendorId);
//        return repairRequests
//                .stream()
//                .map(request -> toResponseDTO(request))
//                .toList();
//    }
//
//    private ResponseDTO toResponseDTO(RepairRequest repairRequest) {
//        ResponseDTO dto = new ResponseDTO();
//
//        dto.setRequestId(repairRequest.getRequestId());
//        dto.setDeviceId(repairRequest.getDeviceId());
//        dto.setRaisedBy(repairRequest.getRaisedBy());
//        dto.setAdminId(repairRequest.getAdminId());
//        dto.setVendorId(repairRequest.getVendorId());
//        dto.setIssueDescription(repairRequest.getIssueDescription());
//
//        if (repairRequest.getStatus() != null) {
//            dto.setStatus(repairRequest.getStatus().name());
//        }
//
//        return dto;
//    }
//
//    //-- helper code
//    private void publishRepairEvent(
//            String eventType,
//            RepairRequest request,
//            RepairStatus previousStatus,
//            RepairStatus newStatus,
//            boolean assignedAutomatically
//    ) {
//        RepairEventDTO event = new RepairEventDTO();
//
//        event.setEventId(UUID.randomUUID().toString());
//        event.setEventType(eventType);
//
//        event.setRepairId(request.getRequestId());
//        event.setDeviceId(request.getDeviceId());
//
//        event.setRaisedBy(request.getRaisedBy());
//        event.setCompanyAdminId(request.getAdminId());
//        event.setVendorId(request.getVendorId());
//
//        event.setIssueDescription(request.getIssueDescription());
//        event.setUrgent(request.isUrgent());
//
//        event.setPreviousStatus(previousStatus);
//        event.setNewStatus(newStatus);
//
//        event.setAssignedAutomatically(assignedAutomatically);
//        event.setTimestamp(LocalDateTime.now());
//
//        repairEventProducer.PublishRepairEvent(event);
//    }
}
