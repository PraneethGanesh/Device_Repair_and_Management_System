package com.example.audit_service.kafka;


import com.example.audit_service.dto.RepairEventDTO;
import com.example.audit_service.entity.AuditRecord;
import com.example.audit_service.repository.AuditRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class RepairEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RepairEventConsumer.class);

    private final AuditRecordRepository auditRecordRepository;

    public RepairEventConsumer(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    @KafkaListener(
            topics = "${kafka.topic.repair-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            @Payload RepairEventDTO event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("[Audit] Received {} | repairId={} | partition={} | offset={}",
                event.getEventType(), event.getRepairId(), partition, offset);

        try {
            AuditRecord record = AuditRecord.from(event);
            auditRecordRepository.save(record);
            log.info("[Audit] Saved audit record for repairId={} eventType={}",
                    event.getRepairId(), event.getEventType());
        } catch (Exception e) {
            log.error("[Audit] Failed to persist event repairId={} eventType={} — {}",
                    event.getRepairId(), event.getEventType(), e.getMessage(), e);
            // Re-throw so Kafka retries via the error handler configured below
            throw e;
        }
    }
}
