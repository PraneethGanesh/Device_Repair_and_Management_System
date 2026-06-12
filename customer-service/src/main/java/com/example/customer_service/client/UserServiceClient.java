package com.example.customer_service.client;

import com.example.customer_service.dto.UserRegistrationRequest;
import com.example.customer_service.dto.UserRegistrationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient userClient;

    public UserServiceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder) {
        this.userClient = loadBalancedRestClientBuilder
                .baseUrl("http://user-service")
                .build();
    }

    public UserRegistrationResponse registerCompanyEmployee(UserRegistrationRequest request) {
        return userClient.post()
                .uri("/api/users/register/COMPANY_EMPLOYEE")
                .body(request)
                .retrieve()
                .body(UserRegistrationResponse.class);
    }
}