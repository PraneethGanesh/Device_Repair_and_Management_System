package com.example.device_service.Service;

import com.example.device_service.Client.CustomerServiceClient;
import com.example.device_service.DTO.*;
import com.example.device_service.Entity.Device;
import com.example.device_service.Entity.DeviceInstance;
import com.example.device_service.Enum.InstanceStatus;
import com.example.device_service.Repository.DeviceInstanceRepository;
import com.example.device_service.Repository.DeviceRepository;
import org.jspecify.annotations.Nullable;
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
    private final CustomerServiceClient customerServiceClient;
    private final DeviceRepository deviceRepository;
    public DeviceInstanceService(DeviceInstanceRepository deviceInstanceRepository, DeviceService deviceService, CustomerServiceClient customerServiceClient, DeviceRepository deviceRepository) {
        this.deviceInstanceRepository = deviceInstanceRepository;
        this.deviceService = deviceService;
        this.customerServiceClient = customerServiceClient;
        this.deviceRepository = deviceRepository;
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

    public List<DeviceInstanceDTO> getDeviceInstancesByCompany(String userId) {
        CompanyResponse companyResponse=customerServiceClient.getCompanyByUserId(userId).getBody();
        List<DeviceInstance> deviceInstances=deviceInstanceRepository.findByCompanyIdAndInstanceStatus(companyResponse.getId(),InstanceStatus.PURCHASED);
        return deviceInstances.stream().map(deviceInstance -> deviceInstanceDTO(deviceInstance)).toList();
    }
    private DeviceInstanceDTO deviceInstanceDTO(DeviceInstance deviceInstance){
        DeviceInstanceDTO deviceInstanceDTO=new DeviceInstanceDTO();
        deviceInstanceDTO.setId(deviceInstance.getId());
        Device device=deviceRepository.findById(deviceInstance.getDevice_id()).orElseThrow();
        deviceInstanceDTO.setDeviceName(device.getDeviceName());
        deviceInstanceDTO.setSerialNumber(deviceInstance.getSerialNumber());
        deviceInstanceDTO.setOrder_id(deviceInstance.getOrder_id());
        deviceInstanceDTO.setStatus(deviceInstance.getStatus());
        deviceInstanceDTO.setCreatedAt(deviceInstance.getCreatedAt());
        return deviceInstanceDTO;
    }

    public ResponseEntity<ResponseDTO> getVendorId(long instanceId) {
        DeviceInstance deviceInstance=deviceInstanceRepository.findById(instanceId).orElseThrow(
                ()->new RuntimeException("not found")
        );
        DeviceResponseDTO deviceResponseDTO= deviceService.getDeviceById(deviceInstance.getDevice_id());
        ResponseDTO responseDTO=new ResponseDTO();
        responseDTO.setVendorId(deviceResponseDTO.getVendorId());
        return ResponseEntity.ok(responseDTO);

    }

    public DeviceInstance updateDeviceStatus(String status, long instanceId) {
        DeviceInstance deviceInstance=deviceInstanceRepository.findById(instanceId).orElseThrow(
                ()->new RuntimeException("not found")
        );
        deviceInstance.setStatus(InstanceStatus.valueOf(status.toUpperCase()));
        return deviceInstanceRepository.save(deviceInstance);
    }
}
