package com.example.notification_service.Consumer;

import com.example.notification_service.DTO.NotificationMessage;
import com.example.notification_service.Service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    private final NotificationService notificationService;

    private static final Logger LOGGER= LoggerFactory.getLogger(MessageConsumer.class);

    public MessageConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = {"${rabbitmq.admin.queue.name}"})
    public void consumeMessage(NotificationMessage notificationMessage){
        LOGGER.info("Notification:",notificationMessage.getMessage());
        notificationService.sendEmail(notificationMessage);
    }
}
