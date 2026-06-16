package com.example.notification_service.Consumer;

import com.example.notification_service.DTO.NotificationMessage;
import com.example.notification_service.Service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MessageConsumer {
    private final NotificationService notificationService;

    private static final Logger LOGGER= LoggerFactory.getLogger(MessageConsumer.class);

    public MessageConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "queue.vendor.specific")
    public void consumeVendorMessage(Map<String, Object> payload) {
        NotificationMessage message = toNotificationMessage(payload);
        LOGGER.info("Vendor notification received: {}", message.getEventType());
        notificationService.sendSingleEmail(
                message.getRecipientEmail(),   // vendor email — already in payload
                message.getTitle(),
                message.getBody()
        );
    }

    @RabbitListener(queues = "queue.company.specific")
    public void consumeCompanyMessage(Map<String, Object> payload) {
        NotificationMessage message = toNotificationMessage(payload);
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
    public void consumeEmployeeMessage(Map<String, Object> payload) {
        NotificationMessage message = toNotificationMessage(payload);
        LOGGER.info("Employee notification received: {}", message.getEventType());
        notificationService.sendSingleEmail(
                message.getRecipientEmail(),
                message.getTitle(),
                message.getBody()
        );
    }

    @RabbitListener(queues = "queue.all.company.admins")
    public void consumeBroadcast(Map<String, Object> payload) {
        NotificationMessage message = toNotificationMessage(payload);
        LOGGER.info("Broadcast notification received: {}", message.getEventType());
        notificationService.sendBulkEmail(
                message.getRecipientEmails(),
                message.getTitle(),
                message.getBody()
        );
    }

    private NotificationMessage toNotificationMessage(Map<String, Object> payload) {
        NotificationMessage message = new NotificationMessage();
        message.setEventType(asString(payload.get("eventType")));
        message.setTitle(asString(payload.getOrDefault("title", "Device Repair Notification")));
        message.setBody(asString(payload.getOrDefault("body", payload.getOrDefault("message", ""))));
        message.setRecipientEmail(asString(payload.get("recipientEmail")));

        Object emails = payload.get("recipientEmails");
        if (emails instanceof List<?> list) {
            message.setRecipientEmails(list.stream()
                    .map(String::valueOf)
                    .toList());
        }
        return message;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
