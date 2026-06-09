package com.example.repair_service.kafka;

import com.example.repair_service.dto.RepairEventDTO;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RepairEventProducer {
    private final KafkaTemplate<String, RepairEventDTO>  kafkaTemplate ;

    @Value("$(kafka.topic.repair-events)")
    private String RepairEventTopic;
    public RepairEventProducer(KafkaTemplate<String, RepairEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void PublishRepairEvent(RepairEventDTO repairEventDTO) {
        String key=String.valueOf(repairEventDTO.getRepairId());
        kafkaTemplate.send(RepairEventTopic, key, repairEventDTO);
        System.out.println("Repair Event Published "+repairEventDTO.getEventType()+" for repair id "+repairEventDTO.getRepairId());
    }
}
