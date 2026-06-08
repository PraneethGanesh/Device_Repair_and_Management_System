package com.example.device_service.Repository;

import com.example.device_service.Entity.DeviceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeviceInstanceRepository extends JpaRepository<DeviceInstance,Long> {
    @Query("select deviceInstance from DeviceInstance deviceInstance where deviceInstance.device_id = :deviceId")
    List<DeviceInstance> findByDevice_id(@Param("deviceId") long deviceId);

    @Query("select deviceInstance from DeviceInstance deviceInstance where deviceInstance.order_id = :orderId")
    List<DeviceInstance> findByOrder_id(@Param("orderId") long orderId);

    @Query("select deviceInstance from DeviceInstance deviceInstance where deviceInstance.company_id = :companyId")
    List<DeviceInstance> findByCompanyId(@Param("companyId") UUID companyId);
}
