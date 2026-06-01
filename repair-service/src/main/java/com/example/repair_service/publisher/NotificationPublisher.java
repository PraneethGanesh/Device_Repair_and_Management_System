package com.example.repair_service.publisher;

import com.example.repair_service.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.repair.raised.routing.key}")
    private String repairRaisedKey;

    @Value("${rabbitmq.repair.acknowledged.routing.key}")
    private String repairAcknowledgedKey;

    @Value("${rabbitmq.repair.completed.routing.key}")
    private String repairCompletedKey;

    @Value("${rabbitmq.repair.closed.routing.key}")
    private String repairClosedKey;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Repair raised → notify ADMIN
    public void publishRepairRaised(NotificationDTO dto) {
        rabbitTemplate.convertAndSend(exchange, repairRaisedKey, dto);
        LOGGER.info("Repair raised event published to exchange: {}", exchange);
    }

    // Admin acknowledged → notify all vendors (broadcast)
    public void publishRepairAcknowledged(NotificationDTO dto) {
        rabbitTemplate.convertAndSend(exchange, repairAcknowledgedKey, dto);
        LOGGER.info("Repair acknowledged event published to exchange: {}", exchange);
    }

    // Vendor completed → notify ADMIN
    public void publishRepairCompleted(NotificationDTO dto) {
        rabbitTemplate.convertAndSend(exchange, repairCompletedKey, dto);
        LOGGER.info("Repair completed event published to exchange: {}", exchange);
    }

    // Admin closed + device reassigned → notify EMPLOYEE
    public void publishRepairClosed(NotificationDTO dto) {
        rabbitTemplate.convertAndSend(exchange, repairClosedKey, dto);
        LOGGER.info("Repair closed event published to exchange: {}", exchange);
    }
}
