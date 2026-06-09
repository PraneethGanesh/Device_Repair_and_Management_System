package com.example.vendor_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange("")
    }
}
