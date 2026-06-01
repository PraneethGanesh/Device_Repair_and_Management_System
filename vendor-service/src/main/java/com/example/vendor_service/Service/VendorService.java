    package com.example.vendor_service.Service;

    import com.example.vendor_service.DTO.DeviceDTO;
    import com.example.vendor_service.DTO.NotificationDTO;
    import com.example.vendor_service.Entity.Role;
    import com.example.vendor_service.Entity.Vendor;
    import com.example.vendor_service.Repository.VendorRepository;
    import org.springframework.cloud.client.loadbalancer.LoadBalanced;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    @Service
    public class VendorService {
        private final VendorRepository vendorRepository;
        private final RestClient deviceClient;
        private final RestClient notificationClient;


        public VendorService(VendorRepository vendorRepository, @LoadBalanced RestClient.Builder restClientBuilder) {
            this.vendorRepository = vendorRepository;
            this.deviceClient = restClientBuilder.baseUrl("http://DEVICE-SERVICE").build();
            this.notificationClient = restClientBuilder.baseUrl("http://NOTIFICATION-SERVICE").build();
        }

        public Vendor createVendor(Vendor vendor){
           return vendorRepository.save(vendor);
        }

        public ResponseEntity<DeviceDTO> addDevice(DeviceDTO deviceDTO){
          ResponseEntity<DeviceDTO> deviceDTOResponseEntity=deviceClient.post()
                  .uri("/api/devices")
                  .body(deviceDTO)
                  .retrieve()
                  .toEntity(DeviceDTO.class);
          NotificationDTO notificationDTO=new NotificationDTO(deviceDTO.getVendorId(),
                  Role.VENDOR,
                  Role.ADMIN,
                  "Device:"+deviceDTO.getDeviceName()+" is added");
          ResponseEntity responseEntity=notificationClient.post()
                  .uri("/api/notifications")
                  .body(notificationDTO)
                  .retrieve()
                  .toBodilessEntity();
            System.out.println("Notification status:"+responseEntity.getStatusCode());
          return deviceDTOResponseEntity;
        }



    }
