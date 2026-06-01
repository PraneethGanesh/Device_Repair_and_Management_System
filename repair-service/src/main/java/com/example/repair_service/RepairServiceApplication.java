package com.example.repair_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RepairServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepairServiceApplication.class, args);
    }
}
