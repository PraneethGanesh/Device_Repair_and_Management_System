package com.example.vendor_service.Publisher;

import com.example.vendor_service.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER= LoggerFactory.getLogger(MessagePublisher.class);
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing.key.admin}")
    private String adminKey;


    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBroadcastMessage(NotificationMessage notificationMessage){
        LOGGER.info("Notification sent !!");
        rabbitTemplate.convertAndSend(exchangeName,adminKey,notificationMessage);
    }
}
