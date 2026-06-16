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

    @RabbitListener(queues = "queue.vendor.specific")
    public void consumeVendorMessage(NotificationMessage message) {
        LOGGER.info("Vendor notification received: {}", message.getEventType());
        notificationService.sendSingleEmail(
                message.getRecipientEmail(),   // vendor email — already in payload
                message.getTitle(),
                message.getBody()
        );
    }

    @RabbitListener(queues = "queue.company.specific")
    public void consumeCompanyMessage(NotificationMessage message) {
        LOGGER.info("Company notification received: {}", message.getEventType());
        if (message.getRecipientEmail() == null || message.getRecipientEmail().isBlank()) {
            LOGGER.warn("Skipping company message — recipientEmail is null. EventType: {}", message.getEventType());
            return;  // ack and discard, don't crash
        }
        notificationService.sendSingleEmail(
                message.getRecipientEmail(),
                message.getTitle(),
                message.getBody()
        );
    }

    @RabbitListener(queues = "queue.employee.specific")
    public void consumeEmployeeMessage(NotificationMessage message) {
        LOGGER.info("Employee notification received: {}", message.getEventType());
        notificationService.sendSingleEmail(
                message.getRecipientEmail(),
                message.getTitle(),
                message.getBody()
        );
    }

    @RabbitListener(queues = "queue.all.company.admins")
    public void consumeBroadcast(NotificationMessage message) {
        LOGGER.info("Broadcast notification received: {}", message.getEventType());
        notificationService.sendBulkEmail(
                message.getRecipientEmails(),
                message.getTitle(),
                message.getBody()
        );
    }
}
