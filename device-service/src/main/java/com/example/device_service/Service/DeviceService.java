package com.example.device_service.Service;

import com.example.device_service.DTO.DeviceDTO;
import com.example.device_service.DTO.DeviceResponseDTO;
import com.example.device_service.DTO.OrderDTO;
import com.example.device_service.Entity.Device;
import com.example.device_service.Exception.DeviceNotFoundException;
import com.example.device_service.Repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
        device.setWarrantyExpiry(deviceDTO.getWarrantyExpiry());
        device.setVendorId(vendorId);
        device.setDeviceType(deviceDTO.getDeviceType());
        device.setStockQuantity(deviceDTO.getStockQuantity());
        Device saved=deviceRepository.save(device);
        return toDeviceDto(saved);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public DeviceResponseDTO getDeviceById(long deviceId) {
        Device device=deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + deviceId));
        return deviceResponseDTO(device);
    }

    private DeviceResponseDTO deviceResponseDTO(Device device){
        DeviceResponseDTO deviceResponseDTO=new DeviceResponseDTO();
        deviceResponseDTO.setVendorId(device.getVendorId());
        deviceResponseDTO.setStockQuantity(device.getStockQuantity());
        return deviceResponseDTO;
    }


    public List<DeviceDTO> getDeviceByVendor(long vendorId) {
        List<Device> devices = deviceRepository.findByVendorId(vendorId);
        return devices.stream()
                .map(this::toDeviceDto)
                .collect(Collectors.toList());
    }


//    public Device updateDevice(long deviceId, DeviceDTO deviceDTO) {
//        Device existingDevice = getDeviceById(deviceId);
//        existingDevice.setDeviceName(deviceDTO.getDeviceName());
//        existingDevice.setDeviceType(deviceDTO.getDeviceType());
//        existingDevice.setWarrantyExpiry(deviceDTO.getWarrantyExpiry());
//        return deviceRepository.save(existingDevice);
//    }
//
//    public void deleteDevice(long deviceId) {
//        Device existingDevice = getDeviceById(deviceId);
//        deviceRepository.delete(existingDevice);
//    }

    private DeviceDTO toDeviceDto(Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceName(device.getDeviceName());
        deviceDTO.setDeviceType(device.getDeviceType());
        deviceDTO.setStockQuantity(device.getStockQuantity());
        deviceDTO.setWarrantyExpiry(device.getWarrantyExpiry());
        return deviceDTO;
    }

    public void reduceDeviceStock(OrderDTO orderDTO) {
        Device device=deviceRepository.findById(orderDTO.getDevice_id()).orElseThrow(
                ()->new RuntimeException("Device not found:"+orderDTO.getDevice_id())
        );
        device.setStockQuantity(device.getStockQuantity() - orderDTO.getQuantity());
        deviceRepository.save(device);
    }
}
