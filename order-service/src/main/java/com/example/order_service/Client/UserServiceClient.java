package com.example.order_service.Client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public class UserServiceClient {
}
