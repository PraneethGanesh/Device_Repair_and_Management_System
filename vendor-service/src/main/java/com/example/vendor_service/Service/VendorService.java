    package com.example.vendor_service.Service;

    import com.example.vendor_service.DTO.*;
    import com.example.vendor_service.Entity.Role;
    import com.example.vendor_service.Entity.Vendor;
    import com.example.vendor_service.Exception.VendorNotFoundException;
    import com.example.vendor_service.Repository.VendorRepository;
    import com.example.vendor_service.Util.JwtUtil;
    import org.springframework.cloud.client.loadbalancer.LoadBalanced;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    import java.util.List;

    @Service
    public class VendorService {
        private final VendorRepository vendorRepository;
        private final RestClient deviceClient;
        private final RestClient notificationClient;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;


        public VendorService(VendorRepository vendorRepository, @LoadBalanced RestClient.Builder restClientBuilder, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
            this.vendorRepository = vendorRepository;
            this.deviceClient = restClientBuilder.clone().baseUrl("http://DEVICE-SERVICE").build();
            this.notificationClient = restClientBuilder.clone().baseUrl("http://NOTIFICATION-SERVICE").build();
            this.passwordEncoder = passwordEncoder;
            this.jwtUtil = jwtUtil;
        }

        public AuthResponse createVendor(VendorDTO vendorDTO){
            Vendor vendor=new Vendor();
            vendor.setVendorName(vendorDTO.getVendorName());
            vendor.setPassword(passwordEncoder.encode(vendorDTO.getPassword()));
            vendor.setEmail(vendorDTO.getEmail());
            vendor.setNumber(vendorDTO.getPhoneNumber());
            Vendor saved=vendorRepository.save(vendor);

            String token= jwtUtil.generate(saved.getEmail(), saved.getRole().name());
            return buildAuthResponse(saved,token);
        }

        private AuthResponse buildAuthResponse(Vendor vendor, String token) {
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setId(vendor.getVendorId());
            response.setName(vendor.getVendorName());
            response.setEmail(vendor.getEmail());
            response.setRole(vendor.getRole().name());
            return response;
        }

        public String login(AuthRequest authRequest){
           Vendor vendor=vendorRepository.findByEmail(authRequest.getUsername())
                   .orElseThrow(
                           ()->new RuntimeException("No account found for: "+authRequest.getUsername())
                   );
            if (!passwordEncoder.matches(authRequest.getPassword(), vendor.getPassword())) {
                throw new BadCredentialsException("Incorrect password");
            }
            String token = jwtUtil.generate(vendor.getEmail(), vendor.getRole().name());
            return token;
        }

        public ResponseEntity<?> addDevice(DeviceDTO deviceDTO,String username,String role){
            if(!role.equals(Role.VENDOR.name())){
                return ResponseEntity.badRequest().body("only vendor has the access to add the device");
            }
            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
                    ()-> new VendorNotFoundException("vendor not found:"+username)
            );

          ResponseEntity<DeviceDTO> deviceDTOResponseEntity=deviceClient.post()
                  .uri("/api/devices/{vendorId}",vendor.getVendorId())
                  .body(deviceDTO)
                  .retrieve()
                  .toEntity(DeviceDTO.class);
          NotificationDTO notificationDTO=new NotificationDTO(vendor.getVendorId(),
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

        public ResponseEntity<?> getDevices(String username,String role){
            if(!role.equals(Role.VENDOR.name())){
                return ResponseEntity.badRequest().body("only vendor has the access to add the device");
            }
            Vendor vendor=vendorRepository.findByEmail(username).orElseThrow(
                    ()-> new VendorNotFoundException("vendor not found:"+username)
            );
          List<DeviceDTO> deviceDTOS= deviceClient.get()
                  .uri("/api/devices/vendor/{vendorId}",vendor.getVendorId())
                  .retrieve()
                  .body(List.class);
          return ResponseEntity.ok(deviceDTOS);
        }



    }
