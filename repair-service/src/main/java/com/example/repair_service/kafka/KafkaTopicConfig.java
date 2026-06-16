package com.example.repair_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.repair-events}")
    private String repairEventsTopic;

    @Bean
    public NewTopic repairEventsTopic() {
        return TopicBuilder.name(repairEventsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
