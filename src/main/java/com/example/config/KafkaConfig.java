package com.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topic.department-events}")
    private String departmentTopic;
    @Value("${spring.kafka.topic.unit-events}")
    private String unitTopic;
    @Value("${spring.kafka.topic.user-events}")
    private String userTopic;

    @Bean
    public NewTopic departmentTopic() {
        return TopicBuilder.name(departmentTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic unitTopic() {
        return TopicBuilder.name(unitTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userTopic() {
        return TopicBuilder.name(userTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
