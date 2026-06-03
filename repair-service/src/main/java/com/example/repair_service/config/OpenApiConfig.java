package com.example.repair_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI repairServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Repair Service API")
                        .version("v1")
                        .description("Repair requests, acknowledgements, vendor assignment, and status updates."));
    }
}
