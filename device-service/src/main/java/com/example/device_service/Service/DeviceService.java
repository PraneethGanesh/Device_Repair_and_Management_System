package com.example.device_service.Service;

import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Exception.DeviceNotFoundException;
import com.example.device_service.Enum.DeviceStatus;
import com.example.device_service.Repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device addDevice(DeviceDTO deviceDTO){
        Device device=new Device();
        device.setDeviceName(device.getDeviceName());
        device.setDeviceType(deviceDTO.getDeviceType());
        device.setDeviceStatus(DeviceStatus.AVAILABLE);
        device.setSerialNumber(UUID.randomUUID().toString());
        device.setWarrantyExpiry(deviceDTO.getWarrantyExpiry());
        device.setVendorId(deviceDTO.getVendorId());
        return deviceRepository.save(device);
    }

    public Device assignDevice(AssignmentRequest assignmentRequest){
        Device device=deviceRepository.findById(assignmentRequest.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException(
                        "Device not found with id: " + assignmentRequest.getDeviceId()));
        device.setAssignedToId(assignmentRequest.getUserId());
        return deviceRepository.save(device);
    }


}
