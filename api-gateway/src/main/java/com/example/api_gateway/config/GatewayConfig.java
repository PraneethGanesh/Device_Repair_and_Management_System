package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Auth / User login+register - PUBLIC
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))


                .route("user-auth", r -> r
                        .path("/api/users/register","/api/users/login")
                        .uri("lb://user-service"))

                // User Service - PROTECTED
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://user-service"))

                // Device Service - PROTECTED
                .route("device-service", r -> r
                        .path("/api/devices/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://device-service"))

                // Vendor Service - PROTECTED
                .route("vendor-service", r -> r
                        .path("/api/vendors/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://vendor-service"))

                // Notification Service - PROTECTED
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://notification-service"))

                // Repair Request Service - PROTECTED
                .route("repair-service", r -> r
                        .path("/api/repairs/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://repair-service"))
                .build();
    }
}