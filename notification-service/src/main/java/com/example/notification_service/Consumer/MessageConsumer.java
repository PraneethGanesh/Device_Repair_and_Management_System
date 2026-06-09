package com.example.notification_service.Consumer;

import com.example.notification_service.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    private static final Logger LOGGER= LoggerFactory.getLogger(MessageConsumer.class);
    @RabbitListener(queues = {"${rabbitmq.admin.queue.name}"})
    public void consumeMessage(NotificationMessage notificationMessage){
        LOGGER.info("Notification:",notificationMessage.getMessage());
    }
}
