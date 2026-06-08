package com.example.device_service.Repository;

import com.example.device_service.Entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {
    List<Device> findByVendorId(long id);

}
