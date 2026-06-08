package com.example.device_service.Service;

import com.example.device_service.Client.CustomerServiceClient;
import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.Entity.DeviceAssignment;
import com.example.device_service.Entity.DeviceInstance;
import com.example.device_service.Enum.AssignmentStatus;
import com.example.device_service.Enum.InstanceStatus;
import com.example.device_service.Repository.DeviceAssignmentRepository;
import com.example.device_service.Repository.DeviceInstanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceAssignmentService {
    private final DeviceAssignmentRepository deviceAssignmentRepository;
    private final DeviceInstanceRepository deviceInstanceRepository;
    private final CustomerServiceClient customerServiceClient;

    public DeviceAssignmentService(
            DeviceAssignmentRepository deviceAssignmentRepository,
            DeviceInstanceRepository deviceInstanceRepository,
            CustomerServiceClient customerServiceClient) {
        this.deviceAssignmentRepository = deviceAssignmentRepository;
        this.deviceInstanceRepository = deviceInstanceRepository;
        this.customerServiceClient = customerServiceClient;
    }

    @Transactional
    public DeviceAssignment assignDevice(UUID companyId, AssignmentRequest request) {
        DeviceInstance deviceInstance = deviceInstanceRepository.findById(request.getDeviceInstanceId())
                .orElseThrow(() -> new RuntimeException("Device instance not found: " + request.getDeviceInstanceId()));

        if (!companyId.equals(deviceInstance.getCompany_id())) {
            throw new RuntimeException("Device instance does not belong to company: " + companyId);
        }
        if (deviceInstance.getStatus() != InstanceStatus.PURCHASED) {
            throw new RuntimeException("Device instance must be PURCHASED before assignment");
        }

        Boolean employeeBelongsToCompany = customerServiceClient
                .employeeBelongsToCompany(request.getEmployeeId(), companyId)
                .getBody();
        if (!Boolean.TRUE.equals(employeeBelongsToCompany)) {
            throw new RuntimeException("Employee does not belong to company: " + companyId);
        }

        boolean alreadyAssigned = deviceAssignmentRepository.existsByDeviceInstanceIdAndStatus(
                deviceInstance.getId(),
                AssignmentStatus.ASSIGNED);
        if (alreadyAssigned) {
            throw new RuntimeException("Device instance is already assigned: " + deviceInstance.getId());
        }

        DeviceAssignment assignment = new DeviceAssignment();
        assignment.setDevice_instance_id(deviceInstance.getId());
        assignment.setCompany_id(companyId);
        assignment.setEmployee_id(request.getEmployeeId());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssigned_at(LocalDateTime.now());

        deviceInstance.setStatus(InstanceStatus.ASSIGNED);
        DeviceAssignment savedAssignment = deviceAssignmentRepository.save(assignment);
        deviceInstanceRepository.save(deviceInstance);

        return savedAssignment;
    }

    public List<DeviceAssignment> getAssignmentsByEmployee(UUID employeeId) {
        return deviceAssignmentRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public DeviceAssignment returnDevice(long assignmentId) {
        DeviceAssignment assignment = deviceAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new RuntimeException("Only assigned devices can be returned");
        }

        DeviceInstance deviceInstance = deviceInstanceRepository.findById(assignment.getDevice_instance_id())
                .orElseThrow(() -> new RuntimeException("Device instance not found: " + assignment.getDevice_instance_id()));

        assignment.setStatus(AssignmentStatus.RETURNED);
        assignment.setReturned_at(LocalDateTime.now());
        deviceInstance.setStatus(InstanceStatus.PURCHASED);

        DeviceAssignment savedAssignment = deviceAssignmentRepository.save(assignment);
        deviceInstanceRepository.save(deviceInstance);

        return savedAssignment;
    }
}
