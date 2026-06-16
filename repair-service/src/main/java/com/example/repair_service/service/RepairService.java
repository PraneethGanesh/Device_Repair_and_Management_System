package com.example.repair_service.service;

import com.example.repair_service.dto.*;
import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RepairStatus;
import com.example.repair_service.feign.CustomerServiceClient;
import com.example.repair_service.feign.DeviceServiceClient;
import com.example.repair_service.feign.VendorServiceClient;
import com.example.repair_service.kafka.RepairEventProducer;
import com.example.repair_service.publisher.NotificationPublisher;
import com.example.repair_service.repository.RepairRequestRepository;
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
    private final VendorServiceClient vendorServiceClient;


    public RepairService(RepairRequestRepository repairRepository,
                         DeviceServiceClient deviceServiceClient,
                         NotificationPublisher notificationPublisher,
                         RepairEventProducer repairEventProducer, CustomerServiceClient customerServiceClient, VendorServiceClient vendorServiceClient) {
        this.repairRepository = repairRepository;
        this.deviceServiceClient = deviceServiceClient;
        this.notificationPublisher = notificationPublisher;
        this.repairEventProducer = repairEventProducer;
        this.customerServiceClient = customerServiceClient;
        this.vendorServiceClient = vendorServiceClient;
    }

//    // ─── 1. Employee raises a repair request ────────────────────────────
    public RepairRequest raiseRequest(RepairRequestDTO dto,String userId) {
        EmployeeDTO response=customerServiceClient.getEmployeeByUserId(userId).getBody();
        UUID employeeId=deviceServiceClient.getDeviceAssignment(dto.getDeviceInstanceId()).getBody();
        if(!employeeId.equals(response.getId())){
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

//        publishRepairEvent("REPAIR_CREATED",
//                request,
//                null,
//                RepairStatus.RAISED,
//                false);


        return repairRepository.save(request);
    }



//    // ─── 2. Admin acknowledges and forwards to vendor pool ────────────────────
    public RepairRequest acknowledgeRequest(long requestId, String userId) {
        RepairRequest request = getById(requestId);
        CompanyResponse response=customerServiceClient.getCompanyByUserId(userId).getBody();
        if (!response.getId().equals(request.getCompanyId())){
            throw new RuntimeException(
                    "Only the company employee belongs to can acknowledge the repair request"
            );
        }
        if (request.getStatus() != RepairStatus.RAISED) {
            throw new IllegalStateException(
                    "Only PENDING requests can be acknowledged. Current status: " + request.getStatus());
        }
        RepairStatus previousStatus = request.getStatus();

        request.setStatus(RepairStatus.ASSIGNED_TO_VENDOR);
        deviceServiceClient.updateDeviceStatus("SENT_TO_REPAIR",request.getDeviceInstanceId());
        RepairRequest saved = repairRepository.save(request);

        publishRepairEvent("REPAIR_ACKNOWLEDGED",
                saved,
                previousStatus,
                RepairStatus.ASSIGNED_TO_VENDOR,
                true);

        return saved;
    }

    public RepairRequest getById(long requestId) {
        return repairRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Repair request not found: " + requestId));
    }


    // ─── 3. Vendor starts work ────────────────────────────────────────────────
    public RepairRequest markInProgress(long requestId,String userId) {
        RepairRequest request = getById(requestId);
        VendorDTO vendorDTO=vendorServiceClient.getVendor(userId).getBody();
        validateVendorOwnership(request, vendorDTO.getId());

        if (request.getStatus() != RepairStatus.ASSIGNED_TO_VENDOR) {
            throw new IllegalStateException(
                    "Request must be ASSIGNED_TO_VENDOR before marking IN_PROGRESS.");
        }

        deviceServiceClient.updateDeviceStatus("UNDER_REPAIR", request.getDeviceInstanceId());
        request.setStatus(RepairStatus.IN_PROGRESS);
        publishRepairEvent("REPAIR_IN_PROGRESS",
                request,
                RepairStatus.ASSIGNED_TO_VENDOR,
                RepairStatus.IN_PROGRESS,
                false);
        return repairRepository.save(request);
    }
    private void validateVendorOwnership(RepairRequest request, long vendorId) {
        if (request.getVendorId() != vendorId) {
            throw new IllegalStateException(
                    "Vendor ID: " + vendorId + " is not assigned to this request.");
        }
    }

    // ─── 4. Vendor marks repair complete ─────────────────────────────────────
    public RepairRequest markCompleted(long requestId,String userId) {
        RepairRequest request = getById(requestId);
        VendorDTO vendorDTO=vendorServiceClient.getVendor(userId).getBody();
        validateVendorOwnership(request, vendorDTO.getId());

        if (request.getStatus() != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Request must be IN_PROGRESS before marking COMPLETED.");
        }

        request.setStatus(RepairStatus.REPAIR_DONE);
        RepairRequest saved = repairRepository.save(request);

        deviceServiceClient.updateDeviceStatus("REPAIRED",request.getDeviceInstanceId());

        publishRepairEvent("REPAIR_COMPLETED",
                saved, RepairStatus.IN_PROGRESS,
                RepairStatus.REPAIR_DONE,
                false);

        return saved;
    }

//    // ─── 5. Admin closes ticket and reassigns device ──────────────────────────
    public RepairRequest closeRequest(long repairId,String userId) {
        RepairRequest request = getById(repairId);
        CompanyResponse response=customerServiceClient.getCompanyByUserId(userId).getBody();
        if (!response.getId().equals(request.getCompanyId())){
            throw new RuntimeException(
                    "Only the company employee belongs to can acknowledge the repair request"
            );
        }
        if (request.getStatus() != RepairStatus.REPAIR_DONE) {
            throw new IllegalStateException(
                    "Only REPAIRED requests can be closed. Current status: " + request.getStatus());
        }

        request.setStatus(RepairStatus.CLOSED);
        RepairRequest saved = repairRepository.save(request);
        deviceServiceClient.updateDeviceStatus("ASSIGNED",request.getDeviceInstanceId());
        publishRepairEvent(
                "DEVICE_RETURNED",
                saved,
                RepairStatus.REPAIR_DONE,
                RepairStatus.CLOSED,
                false
        );

        return saved;
    }

    public List<RepairRequest> getOpenRequestsByCompany(String userId) {
        CompanyResponse response=customerServiceClient.getCompanyByUserId(userId).getBody();
        UUID companyId=response.getId();
        List<RepairRequest> repairRequests=repairRepository.findByCompanyIdAndStatus(companyId,RepairStatus.RAISED);
        return repairRequests;
    }

    public List<RepairRequest> getRequestsAssignedToVendor(String userId) {
        VendorDTO vendorDTO=vendorServiceClient.getVendor(userId).getBody();
        List<RepairRequest> repairRequests=repairRepository.findByStatusAndVendorId(RepairStatus.ASSIGNED_TO_VENDOR,vendorDTO.getId());
        return repairRequests;
    }

    public List<RepairRequest> getRequestsInProgress(String userId) {
        VendorDTO vendorDTO=vendorServiceClient.getVendor(userId).getBody();
        List<RepairRequest> repairRequests=repairRepository.findByStatusAndVendorId(RepairStatus.IN_PROGRESS,vendorDTO.getId());
        return repairRequests;
    }

    public List<RepairRequest> getRequestsRepaired(String userId) {
        CompanyResponse response=customerServiceClient.getCompanyByUserId(userId).getBody();
        UUID companyId=response.getId();
        List<RepairRequest> repairRequests=repairRepository.findByCompanyIdAndStatus(companyId,RepairStatus.REPAIR_DONE);
        return  repairRequests;
    }
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
//
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
private void publishRepairEvent(
        String eventType,
        RepairRequest request,
        RepairStatus previousStatus,
        RepairStatus newStatus,
        boolean assignedAutomatically
) {
    RepairEventDTO event = new RepairEventDTO();
    event.setEventId(UUID.randomUUID().toString());
    event.setEventType(eventType);
    event.setRepairId(request.getId());
    event.setDeviceId(request.getDeviceInstanceId());
    event.setRaisedBy(request.getRaisedBy());
    event.setVendorId(request.getVendorId());
    event.setIssueDescription(request.getIssueDescription());
    event.setPreviousStatus(previousStatus);
    event.setNewStatus(newStatus);
    event.setAssignedAutomatically(assignedAutomatically);
    event.setTimestamp(LocalDateTime.now());
    repairEventProducer.PublishRepairEvent(event);
}
}
