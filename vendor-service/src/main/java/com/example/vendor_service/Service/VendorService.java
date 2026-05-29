package com.example.vendor_service.Service;

import com.example.vendor_service.DTO.DeviceDTO;
import com.example.vendor_service.Entity.Vendor;
import com.example.vendor_service.Repository.VendorRepository;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class VendorService {
    private final VendorRepository vendorRepository;
    private final RestClient restClient;


    public VendorService(VendorRepository vendorRepository, @LoadBalanced RestClient.Builder restClientBuilder) {
        this.vendorRepository = vendorRepository;
        this.restClient = restClientBuilder.baseUrl("http://DEVICE-SERVICE").build();
    }

    public Vendor createVendor(Vendor vendor){
       return vendorRepository.save(vendor);
    }

    public ResponseEntity<DeviceDTO> addDevice(DeviceDTO deviceDTO){
      ResponseEntity<DeviceDTO> deviceDTOResponseEntity=restClient.post()
              .uri("/api/devices")
              .body(deviceDTO)
              .retrieve()
              .toEntity(DeviceDTO.class);
      return deviceDTOResponseEntity;
    }



}
