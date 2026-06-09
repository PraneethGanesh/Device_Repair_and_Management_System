package com.example.device_service.Service;

import com.example.device_service.DTO.OrderDTO;
import com.example.device_service.Entity.DeviceInstance;
import com.example.device_service.Enum.InstanceStatus;
import com.example.device_service.Repository.DeviceInstanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceInstanceService {
    private final DeviceInstanceRepository deviceInstanceRepository;
    private final DeviceService deviceService;
    public DeviceInstanceService(DeviceInstanceRepository deviceInstanceRepository, DeviceService deviceService) {
        this.deviceInstanceRepository = deviceInstanceRepository;
        this.deviceService = deviceService;
    }

    public ResponseEntity<String> addDeviceInstance(OrderDTO orderDTO) {
        List<DeviceInstance> instances = new ArrayList<>();
        int quantity= orderDTO.getQuantity();
        for(int i=0;i<quantity;i++){
            DeviceInstance instance = new DeviceInstance();
            instance.setCompany_id(orderDTO.getCompany_id());
            instance.setDevice_id(orderDTO.getDevice_id());
            instance.setOrder_id(orderDTO.getOrder_id());
            instance.setStatus(InstanceStatus.RESERVED);
            instance.setSerialNumber(UUID.randomUUID().toString());
            instance.setCreatedAt(LocalDateTime.now());
            instances.add(instance);
        }
        deviceInstanceRepository.saveAll(instances);
        deviceService.reduceDeviceStock(orderDTO);
        return ResponseEntity.ok("Added the device instances with status reserved");
    }

    public ResponseEntity<String> updateDeviceInstance(long orderId) {
        List<DeviceInstance> instances=deviceInstanceRepository.findByOrder_id(orderId);
        for(int i=0;i<instances.size();i++){
            DeviceInstance instance= instances.get(i);
            instance.setStatus(InstanceStatus.PURCHASED);
            deviceInstanceRepository.save(instance);
        }
        return ResponseEntity.ok("update the device status to purchased");
    }

    public List<DeviceInstance> getDeviceInstancesByCompany(UUID companyId) {
        return deviceInstanceRepository.findByCompanyId(companyId);
    }
}
