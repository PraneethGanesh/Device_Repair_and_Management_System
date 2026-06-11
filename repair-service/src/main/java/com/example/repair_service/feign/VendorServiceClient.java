package com.example.repair_service.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "vendor-service")
public interface VendorServiceClient {

}
