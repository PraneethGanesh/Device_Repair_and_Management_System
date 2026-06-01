package com.example.user_service.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.device.assigned.routing.key}")
    private String deviceAssignedkey;
    @Value("${rabbitmq.device.added.routing.key}")
    private String deviceAddedkey;
    @Value("${rabbitmq.admin.queue}")
    private String adminQueue;
    @Value("${rabbitmq.employee.queue}")
    private String employeeQueue;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED); // ← directly on converter
        return converter;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue adminQueue() {
        return new Queue(adminQueue);
    }

    @Bean
    public Queue employeeQueue() {
        return new Queue(employeeQueue);
    }

    @Bean
    public Binding adminBinding() {
        return BindingBuilder.bind(adminQueue()).to(exchange()).with(deviceAddedkey);
    }

    @Bean
    public Binding employeeBinding() {
        return BindingBuilder.bind(employeeQueue()).to(exchange()).with(deviceAssignedkey);
    }
}