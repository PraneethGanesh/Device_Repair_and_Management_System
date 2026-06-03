package com.example.vendor_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vendorServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vendor Service API")
                        .version("v1")
                        .description("Vendor authentication, vendor-owned devices, and repair progress endpoints."));
    }
}
