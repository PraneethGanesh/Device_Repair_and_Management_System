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
    public static final String EXCHANGE = "notification.exchange";

    public static final String KEY_BROADCAST         = "notification.broadcast";
    public static final String KEY_VENDOR            = "notification.vendor.#";
    public static final String KEY_COMPANY           = "notification.company.#";
    public static final String KEY_EMPLOYEE          = "notification.employee.#";

    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBroadcastMessage(NotificationMessage notificationMessage){
        LOGGER.info("Notification sent !!");
        rabbitTemplate.convertAndSend(EXCHANGE,KEY_BROADCAST,notificationMessage);
    }
}
