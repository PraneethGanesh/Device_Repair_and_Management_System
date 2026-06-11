package com.example.order_service.Publisher;

import com.example.order_service.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER= LoggerFactory.getLogger(MessageProducer.class);
    @Value("${rabbitmq.routing.key.company}")
    private String key;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCompanyMessage(NotificationMessage notificationMessage){
        LOGGER.info("Notification sent: {}",notificationMessage.getEventType());
        rabbitTemplate.convertAndSend(exchangeName,key,notificationMessage);
    }
}
