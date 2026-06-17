package com.example.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "notification.exchange";

    public static final String QUEUE_BROADCAST       = "queue.all.company.admins";
    public static final String QUEUE_VENDOR          = "queue.vendor.specific";
    public static final String QUEUE_COMPANY         = "queue.company.specific";
    public static final String QUEUE_EMPLOYEE        = "queue.employee.specific";

    public static final String KEY_BROADCAST         = "notification.broadcast";
    public static final String KEY_VENDOR            = "notification.vendor.#";
    public static final String KEY_COMPANY           = "notification.company.#";
    public static final String KEY_EMPLOYEE          = "notification.employee.#";


    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ── Queues ──
    @Bean
    public Queue broadcastQueue() {
        return new Queue(QUEUE_BROADCAST, true);
    }

    @Bean
    public Queue vendorQueue() {
        return new Queue(QUEUE_VENDOR, true);
    }

    @Bean
    public Queue companyQueue() {
        return new Queue(QUEUE_COMPANY, true);
    }

    @Bean
    public Queue employeeQueue() {
        return new Queue(QUEUE_EMPLOYEE, true);
    }

    @Bean
    public Binding broadcastBinding() {
        return BindingBuilder
                .bind(broadcastQueue())
                .to(notificationExchange())
                .with(KEY_BROADCAST);
    }

    @Bean
    public Binding vendorBinding() {
        return BindingBuilder
                .bind(vendorQueue())
                .to(notificationExchange())
                .with(KEY_VENDOR);
        // matches notification.vendor.123
        //         notification.vendor.456 etc.
    }

    @Bean
    public Binding companyBinding() {
        return BindingBuilder
                .bind(companyQueue())
                .to(notificationExchange())
                .with(KEY_COMPANY);
        // matches notification.company.10
        //         notification.company.20 etc.
    }

    @Bean
    public Binding employeeBinding() {
        return BindingBuilder
                .bind(employeeQueue())
                .to(notificationExchange())
                .with(KEY_EMPLOYEE);
        // matches notification.employee.55 etc.
    }
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
