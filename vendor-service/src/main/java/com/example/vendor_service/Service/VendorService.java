    package com.example.vendor_service.Service;

    import com.example.vendor_service.DTO.*;
    import com.example.vendor_service.Entity.Vendor;
    import com.example.vendor_service.Enum.ApprovalStatus;
    import com.example.vendor_service.Exception.VendorNotFoundException;
    import com.example.vendor_service.Publisher.MessagePublisher;
    import com.example.vendor_service.Repository.VendorRepository;
    import org.springframework.cloud.client.loadbalancer.LoadBalanced;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    @Service
    public class VendorService {
        private final VendorRepository vendorRepository;
        private final RestClient deviceClient;
        private final RestClient orderClient;
        private final MessagePublisher messagePublisher;

        public VendorService(VendorRepository vendorRepository, @LoadBalanced RestClient.Builder restClientBuilder, MessagePublisher messagePublisher) {
            this.vendorRepository = vendorRepository;
            this.deviceClient=restClientBuilder.clone().baseUrl("http://device-service").build();
            this.orderClient=restClientBuilder.clone().baseUrl("http://order-service").build();
            this.messagePublisher = messagePublisher;
        }

        public Vendor registerVendor(RegisterDTO registerDTO,
                                     String username,
                                     String userId){
            Vendor vendor=new Vendor();
            vendor.setUserId(userId);
            vendor.setCompanyName(registerDTO.getCompanyName());
            vendor.setEmail(username);
            vendor.setGstNumber(registerDTO.getGstNumber());
            vendor.setPhone(registerDTO.getPhone());
            vendor.setAddress(registerDTO.getAddress());
            return vendorRepository.save(vendor);
        }

        public ResponseEntity<?> approveVendor(ActionDTO actionDTO, String role) {
            if(!role.equalsIgnoreCase("ADMIN")){
             return ResponseEntity.badRequest().body("Only admin can approve or reject the vendor account");
            }
            Vendor vendor=vendorRepository.findById(actionDTO.getVendorId()).orElseThrow(
                    ()->new VendorNotFoundException("vendor with id:"+actionDTO.getVendorId())
            );

            vendor.setApprovalStatus(ApprovalStatus.valueOf(actionDTO.getAction().toUpperCase()));
            vendor.setOnboardedAt(LocalDateTime.now());
            Vendor saved=vendorRepository.save(vendor);
            return ResponseEntity.ok(saved);
        }

        public ResponseEntity<?> myAccount(String userId) {
            Vendor vendor=vendorRepository.findByUserId(userId).orElseThrow(
                    ()->new VendorNotFoundException("vendor with id: "+userId)
            );
            return ResponseEntity.ok(
                    Map.of("Approval status:",vendor.getApprovalStatus().equals(ApprovalStatus.APPROVED)?"Account approved by Admin":" Account Not approved By admin",
                            "Vendor Account:",vendor)
            );
        }

        public ResponseEntity<?> getPendingAccount(String role) {
            if(!role.equalsIgnoreCase("ADMIN")){
                return ResponseEntity.badRequest().body("Only admin can see the pending vendor account");
            }
            List<Vendor> vendorsList=vendorRepository.findByApproval(ApprovalStatus.PENDING.name());
            List<VendorResponseDTO> vendorResponseDTOS=vendorsList.stream()
                    .map(vendor -> toVendorResponseDTO(vendor))
                    .toList();
            return ResponseEntity.ok(vendorResponseDTOS);
        }

        private VendorResponseDTO toVendorResponseDTO(Vendor vendor){
            VendorResponseDTO vendorResponseDTO=new VendorResponseDTO();
            vendorResponseDTO.setId(vendor.getId());
            vendorResponseDTO.setCompanyName(vendor.getCompanyName());
            vendorResponseDTO.setEmail(vendor.getEmail());
            vendorResponseDTO.setPhone(vendor.getPhone());
            vendorResponseDTO.setGstNumber(vendor.getGstNumber());
            return vendorResponseDTO;
        }

        public ResponseEntity<?> addDevice(DeviceDTO deviceDTO, String username, String role){
            if(!role.equalsIgnoreCase("VENDOR")){
                return ResponseEntity.badRequest().body("only vendor has the access to add the device");
            }
            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
                    ()-> new VendorNotFoundException("vendor not found:"+username)
            );

          ResponseEntity<DeviceDTO> deviceDTOResponseEntity=deviceClient.post()
                  .uri("/api/devices/{vendorId}",vendor.getId())
                  .body(deviceDTO)
                  .retrieve()
                  .toEntity(DeviceDTO.class);
          NotificationMessage notificationMessage=new NotificationMessage();
          notificationMessage.setSenderName(vendor.getCompanyName());
          notificationMessage.setReceiverType("COMPANY_ADMIN");
          notificationMessage.setTitle("Device added by the vendor:"+vendor.getCompanyName());
          notificationMessage.setMessage("Device:"+deviceDTO.getDeviceName()+", Quantity:"+deviceDTO.getStockQuantity());
          messagePublisher.publishMessage(notificationMessage);
          return deviceDTOResponseEntity;
        }

        public ResponseEntity<?> getDevices(String username,String role){
            if(!role.equalsIgnoreCase("VENDOR")){
                return ResponseEntity.badRequest().body("only vendor has the access to see his device");
            }
            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
                    ()-> new VendorNotFoundException("vendor not found:"+username)
            );
            if(vendor.getApprovalStatus().equals(ApprovalStatus.PENDING)){
                return ResponseEntity.badRequest().body("Vendor not approved yet!!");
            }
          List<DeviceDTO> deviceDTOS= deviceClient.get()
                  .uri("/api/devices/vendor/{vendorId}",vendor.getId())
                  .retrieve()
                  .body(List.class);
          return ResponseEntity.ok(deviceDTOS);
        }

        public ResponseEntity<String> acceptOrders(long orderId,String userId) {
            Vendor vendor=vendorRepository.findByUserId(userId).orElseThrow(
                    ()->new VendorNotFoundException("vendor with id: "+userId)
            );
            String response=orderClient.put()
                    .uri("/api/order/{orderId}/{vendorId}",orderId,vendor.getId())
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(response);
        }

        public List<OrderDTO> getOrders(String userId) {
            Vendor vendor=vendorRepository.findByUserId(userId).orElseThrow(
                    ()->new VendorNotFoundException("vendor with id: "+userId)
            );
            List<OrderDTO> orderDTOS=orderClient
                    .get()
                    .uri("/api/order/{vendorId}",vendor.getId())
                    .retrieve()
                    .body(List.class);
            return orderDTOS;
        }


//        private final VendorRepository vendorRepository;
//        private final RestClient deviceClient;
//        private final RestClient notificationClient;
//        private final RestClient repairClient;
//        private final PasswordEncoder passwordEncoder;
//        private final JwtUtil jwtUtil;
//
//
//        public VendorService(VendorRepository vendorRepository, @LoadBalanced RestClient.Builder restClientBuilder, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
//            this.vendorRepository = vendorRepository;
//            this.deviceClient = restClientBuilder.clone().baseUrl("http://DEVICE-SERVICE").build();
//            this.notificationClient = restClientBuilder.clone().baseUrl("http://NOTIFICATION-SERVICE").build();
//            this.repairClient = restClientBuilder.clone().baseUrl("http://REPAIR-SERVICE").build();
//            this.passwordEncoder = passwordEncoder;
//            this.jwtUtil = jwtUtil;
//        }
//
//        public AuthResponse createVendor(VendorDTO vendorDTO){
//            Vendor vendor=new Vendor();
//            vendor.setVendorName(vendorDTO.getVendorName());
//            vendor.setPassword(passwordEncoder.encode(vendorDTO.getPassword()));
//            vendor.setEmail(vendorDTO.getEmail());
//            vendor.setNumber(vendorDTO.getPhoneNumber());
//            Vendor saved=vendorRepository.save(vendor);
//
//            String token= jwtUtil.generate(saved.getEmail(), saved.getRole().name());
//            return buildAuthResponse(saved,token);
//        }
//
//        public ResponseEntity<?> addDevice(DeviceDTO deviceDTO,String username,String role){
//            if(!role.equals(Role.VENDOR.name())){
//                return ResponseEntity.badRequest().body("only vendor has the access to add the device");
//            }
//            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
//                    ()-> new VendorNotFoundException("vendor not found:"+username)
//            );
//
//          ResponseEntity<DeviceDTO> deviceDTOResponseEntity=deviceClient.post()
//                  .uri("/api/devices/{vendorId}",vendor.getVendorId())
//                  .body(deviceDTO)
//                  .retrieve()
//                  .toEntity(DeviceDTO.class);
//          NotificationDTO notificationDTO=new NotificationDTO(vendor.getVendorId(),
//                  Role.VENDOR,
//                  Role.ADMIN,
//                  "Device:"+deviceDTO.getDeviceName()+" is added");
//          ResponseEntity responseEntity=notificationClient.post()
//                  .uri("/api/notifications")
//                  .body(notificationDTO)
//                  .retrieve()
//                  .toBodilessEntity();
//            System.out.println("Notification status:"+responseEntity.getStatusCode());
//          return deviceDTOResponseEntity;
//        }
//
//        public ResponseEntity<?> getDevices(String username,String role){
//            if(!role.equals(Role.VENDOR.name())){
//                return ResponseEntity.badRequest().body("only vendor has the access to see his device");
//            }
//            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
//                    ()-> new VendorNotFoundException("vendor not found:"+username)
//            );
//          List<DeviceDTO> deviceDTOS= deviceClient.get()
//                  .uri("/api/devices/vendor/{vendorId}",vendor.getVendorId())
//                  .retrieve()
//                  .body(List.class);
//          return ResponseEntity.ok(deviceDTOS);
//        }
//
//
//        public ResponseEntity<?> getMyprofile(String username) {
//            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
//                    ()-> new VendorNotFoundException("vendor not found:"+username)
//            );
//
//            return ResponseEntity.ok(vendorResponseDTO(vendor));
//
//        }
//
//        private VendorResponseDTO vendorResponseDTO(Vendor vendor){
//            VendorResponseDTO vendorResponseDTO=new VendorResponseDTO();
//            vendorResponseDTO.setVendorName(vendor.getVendorName());
//            vendorResponseDTO.setEmail(vendor.getEmail());
//            vendorResponseDTO.setNumber(vendor.getNumber());
//            return vendorResponseDTO;
//        }
//
//        public ResponseEntity<?> markInProgress(String username, long repairId) {
//            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
//                    ()-> new VendorNotFoundException("vendor not found:"+username)
//            );
//            UpdateRepairStatusRequest updateRepairStatusRequest=new UpdateRepairStatusRequest();
//            updateRepairStatusRequest.setRepairId(repairId);
//            updateRepairStatusRequest.setVendorId(vendor.getVendorId());
//            ResponseEntity responseEntity=repairClient.put()
//                    .uri("/api/repairs/progress")
//                    .body(updateRepairStatusRequest)
//                    .retrieve()
//                    .toBodilessEntity();
//            System.out.println(responseEntity.getStatusCode());
//            return ResponseEntity.ok("Repair request:"+repairId+" marked as in progress");
//
//        }
//
//        public ResponseEntity<?> markCompleted(String username, long repairId) {
//            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
//                    ()-> new VendorNotFoundException("vendor not found:"+username)
//            );
//            UpdateRepairStatusRequest updateRepairStatusRequest=new UpdateRepairStatusRequest();
//            updateRepairStatusRequest.setRepairId(repairId);
//            updateRepairStatusRequest.setVendorId(vendor.getVendorId());
//            ResponseEntity responseEntity=repairClient.put()
//                    .uri("/api/repairs/complete")
//                    .body(updateRepairStatusRequest)
//                    .retrieve()
//                    .toBodilessEntity();
//            System.out.println(responseEntity.getStatusCode());
//            return ResponseEntity.ok("Repair request:"+repairId+" marked as in completed");
//        }
    }
