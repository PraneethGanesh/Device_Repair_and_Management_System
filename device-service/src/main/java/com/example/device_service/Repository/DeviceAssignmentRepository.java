package com.example.device_service.Repository;

import com.example.device_service.Entity.DeviceAssignment;
import com.example.device_service.Enum.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeviceAssignmentRepository extends JpaRepository<DeviceAssignment, Long> {
    @Query("select count(assignment) > 0 from DeviceAssignment assignment where assignment.device_instance_id = :deviceInstanceId and assignment.status = :status")
    boolean existsByDeviceInstanceIdAndStatus(
            @Param("deviceInstanceId") long deviceInstanceId,
            @Param("status") AssignmentStatus status);

    @Query("select assignment from DeviceAssignment assignment where assignment.employee_id = :employeeId")
    List<DeviceAssignment> findByEmployeeId(@Param("employeeId") UUID employeeId);
}
