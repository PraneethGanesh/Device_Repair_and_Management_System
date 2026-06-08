package com.example.device_service.Repository;

import com.example.device_service.Entity.DeviceInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceInstanceRepository extends JpaRepository<DeviceInstance,Long> {
    List<DeviceInstance> findByDevice_id(long deviceId);
}
