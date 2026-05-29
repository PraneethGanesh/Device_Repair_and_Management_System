package com.example.device_service.Controller;

import com.example.device_service.Service.DeviceService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }



}
