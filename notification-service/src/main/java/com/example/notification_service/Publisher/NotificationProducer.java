package com.example.notification_service.Publisher;

import com.example.notification_service.Entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger LOGGER= LoggerFactory.getLogger(NotificationProducer.class);

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.device.assigned.routing.key}")
    private String deviceAssignedkey;
    @Value("${rabbitmq.device.added.routing.key}")
    private String deviceAddedkey;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Notification saved){
        String routingKey=resolveRoutingKey(saved);
        rabbitTemplate.convertAndSend(exchange,routingKey,saved);
        LOGGER.info("notification sent to Queue:{}", saved.getRecipientType().toString().toLowerCase());
    }

    private String resolveRoutingKey(Notification saved) {
       return switch (saved.getRecipientType()){
           case ADMIN-> deviceAddedkey;
           case EMPLOYEE-> deviceAssignedkey;
           default -> throw new IllegalStateException("Unexpected value: " + saved.getRecipientType());
       };
    }
}
