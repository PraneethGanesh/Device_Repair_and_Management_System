package com.example.vendor_service.DTO;

import com.example.vendor_service.Enum.DeviceType;

import java.time.LocalDate;

public class DeviceDTO {
    private String DeviceName;
    private DeviceType deviceType;
    private LocalDate warrantyExpiry;
}
