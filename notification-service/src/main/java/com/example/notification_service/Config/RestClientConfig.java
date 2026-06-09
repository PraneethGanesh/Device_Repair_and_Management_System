package com.example.notification_service.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    @LoadBalanced
    public RestClient.Builder loadbalancedRestClient(){
        return RestClient.builder();
    }
    @Bean
    @Primary
    public RestClient.Builder restClient(){
        return RestClient.builder();
    }
}
