package com.example.device_service.Service;

import com.example.device_service.DTO.AssignmentRequest;
import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.DTO.DeviceStatusDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Exception.DeviceNotFoundException;
import com.example.device_service.Enum.DeviceStatus;
import com.example.device_service.Repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDTO addDevice(DeviceDTO deviceDTO,long vendorId){
        Device device=new Device();
        device.setDeviceName(deviceDTO.getDeviceName());
        device.setDeviceType(deviceDTO.getDeviceType());
        device.setDeviceStatus(DeviceStatus.AVAILABLE);
        device.setWarrantyExpiry(deviceDTO.getWarrantyExpiry());
        device.setVendorId(vendorId);
        Device saved=deviceRepository.save(device);
        return toDeviceDto(saved);
    }
    private DeviceDTO ToDeviceDTO(Device device){
        DeviceDTO deviceDTO=new DeviceDTO();
        deviceDTO.setDeviceName(device.getDeviceName());
        deviceDTO.setDeviceType(device.getDeviceType());
        deviceDTO.setWarrantyExpiry(device.getWarrantyExpiry());
        return deviceDTO;
    }

    public DeviceDTO assignDevice(AssignmentRequest assignmentRequest){
        Device device=deviceRepository.findById(assignmentRequest.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException(
                        "Device not found with id: " + assignmentRequest.getDeviceId()));
        device.setDeviceStatus(DeviceStatus.ASSIGNED);
        Device saved=deviceRepository.save(device);
        return toDeviceDto(saved);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + deviceId));
    }

    public List<DeviceDTO> getDeviceByVendor(long vendorId) {
        List<Device> devices = deviceRepository.findByVendorId(vendorId);
        return devices.stream()
                .map(this::toDeviceDto)
                .collect(Collectors.toList());
    }


    public Device updateDevice(long deviceId, DeviceDTO deviceDTO) {
        Device existingDevice = getDeviceById(deviceId);
        existingDevice.setDeviceName(deviceDTO.getDeviceName());
        existingDevice.setDeviceType(deviceDTO.getDeviceType());
        existingDevice.setWarrantyExpiry(deviceDTO.getWarrantyExpiry());
        return deviceRepository.save(existingDevice);
    }

    public void deleteDevice(long deviceId) {
        Device existingDevice = getDeviceById(deviceId);
        deviceRepository.delete(existingDevice);
    }

    private DeviceDTO toDeviceDto(Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceName(device.getDeviceName());
        deviceDTO.setDeviceType(device.getDeviceType());
        deviceDTO.setWarrantyExpiry(device.getWarrantyExpiry());
        return deviceDTO;
    }

    public List<Device> getDevicesByEmployeeId(long employeeId) {
        return deviceRepository.findByAssignedToId(employeeId);
    }

    public Device updateDeviceStatus(DeviceStatusDTO statusDTO) {
        Device device = getDeviceById(statusDTO.getDeviceId());
        device.setDeviceStatus(DeviceStatus.valueOf(statusDTO.getStatus()));
        return deviceRepository.save(device);
    }

    public List<DeviceDTO> getDevicesByEmployee(long employeeId) {
        return deviceRepository.findByAssignedToId(employeeId)
                .stream()
                .map(this::toDeviceDto)
                .toList();
    }
}
