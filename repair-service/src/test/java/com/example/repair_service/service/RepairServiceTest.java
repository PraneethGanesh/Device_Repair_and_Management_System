package com.example.repair_service.service;

import com.example.repair_service.dto.AssignmentRequestDTO;
import com.example.repair_service.dto.CloseRepairDTO;
import com.example.repair_service.dto.DeviceStatusDTO;
import com.example.repair_service.dto.RepairRequestDTO;
import com.example.repair_service.dto.UpdateRepairStatusRequest;
import com.example.repair_service.entity.RepairRequest;
import com.example.repair_service.enums.RepairStatus;
import com.example.repair_service.feign.DeviceServiceClient;
import com.example.repair_service.publisher.NotificationPublisher;
import com.example.repair_service.repository.RepairRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairServiceTest {

    @Mock
    private RepairRequestRepository repairRepository;

    @Mock
    private DeviceServiceClient deviceServiceClient;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private RepairService repairService;

//    @Test
//    void raiseRequestCreatesPendingRepair() {
//        RepairRequestDTO dto = new RepairRequestDTO();
//        dto.setDeviceId(10L);
//        dto.setIssueDescription("Screen flickers");
//        dto.setUrgent(true);
//
//        when(repairRepository.save(any(RepairRequest.class))).thenAnswer(invocation -> {
//            RepairRequest request = invocation.getArgument(0);
//            request.setRequestId(1L);
//            return request;
//        });
//
//        RepairRequest result = repairService.raiseRequest(dto, 20L, 50L);
//
//        assertThat(result.getStatus()).isEqualTo(RepairStatus.PENDING);
//        assertThat(result.getRaisedBy()).isEqualTo(20L);
//        assertThat(result.getVendorId()).isEqualTo(50L);
//        assertThat(result.isUrgent()).isTrue();
//
//        verify(deviceServiceClient, never()).updateDeviceStatus(anyLong(), any());
//        verify(notificationPublisher).publishRepairRaised(any());
//    }

//    @Test
//    void acknowledgeRequestOnlyAllowsPendingRequests() {
//        RepairRequest request = repairWithStatus(1L, RepairStatus.PENDING);
//        when(repairRepository.findById(1L)).thenReturn(Optional.of(request));
//        when(repairRepository.save(request)).thenReturn(request);
//
//        RepairRequest result = repairService.acknowledgeRequest(1L, 99L);
//
//        assertThat(result.getStatus()).isEqualTo(RepairStatus.ASSIGNED_TO_VENDOR);
//        assertThat(result.getAdminId()).isEqualTo(99L);
//        verify(notificationPublisher).publishRepairAcknowledged(any());
//    }
//
//    @Test
//    void assignVendorRejectsUnavailableRequests() {
//        RepairRequest request = repairWithStatus(1L, RepairStatus.IN_PROGRESS);
//        when(repairRepository.findById(1L)).thenReturn(Optional.of(request));
//
//        assertThatThrownBy(() -> repairService.assignVendor(1L, 50L))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("not available");
//
//        verify(repairRepository, never()).save(any());
//    }

//    @Test
//    void vendorCanCompleteOnlyOwnInProgressRequest() {
//        RepairRequest request = repairWithStatus(1L, RepairStatus.IN_PROGRESS);
//        request.setVendorId(50L);
//        when(repairRepository.findById(1L)).thenReturn(Optional.of(request));
//        when(repairRepository.save(request)).thenReturn(request);
//
//        UpdateRepairStatusRequest updateRequest = new UpdateRepairStatusRequest();
//        updateRequest.setRepairId(1L);
//        updateRequest.setVendorId(50L);
//
//        RepairRequest result = repairService.markCompleted(updateRequest);
//
//        assertThat(result.getStatus()).isEqualTo(RepairStatus.COMPLETED);
//        ArgumentCaptor<DeviceStatusDTO> statusCaptor = ArgumentCaptor.forClass(DeviceStatusDTO.class);
//        verify(deviceServiceClient).updateDeviceStatus(eq(10L), statusCaptor.capture());
//        assertThat(statusCaptor.getValue().getStatus()).isEqualTo("REPAIR_DONE");
//        verify(notificationPublisher).publishRepairCompleted(any());
//    }

//    @Test
//    void closeRequestReassignsDeviceBeforeClosingTicket() {
//        RepairRequest request = repairWithStatus(1L, RepairStatus.COMPLETED);
//        when(repairRepository.findById(1L)).thenReturn(Optional.of(request));
//        when(repairRepository.save(request)).thenReturn(request);
//
//        CloseRepairDTO dto = new CloseRepairDTO();
//        dto.setAdminId(99L);
//        dto.setAssignToEmployeeId(20L);
//
//        RepairRequest result = repairService.closeRequest(1L, dto);
//
//        assertThat(result.getStatus()).isEqualTo(RepairStatus.CLOSED);
//
//        ArgumentCaptor<AssignmentRequestDTO> assignmentCaptor = ArgumentCaptor.forClass(AssignmentRequestDTO.class);
//        verify(deviceServiceClient).assignDevice(assignmentCaptor.capture());
//        assertThat(assignmentCaptor.getValue().getDeviceId()).isEqualTo(10L);
//        assertThat(assignmentCaptor.getValue().getUserId()).isEqualTo(20L);
//        verify(deviceServiceClient, never()).updateDeviceStatus(anyLong(), any());
//        verify(notificationPublisher).publishRepairClosed(any());
//    }
//
//    private RepairRequest repairWithStatus(long requestId, RepairStatus status) {
//        RepairRequest request = new RepairRequest();
//        request.setRequestId(requestId);
//        request.setDeviceId(10L);
//        request.setRaisedBy(20L);
//        request.setIssueDescription("Screen flickers");
//        request.setStatus(status);
//        return request;
//    }
}
