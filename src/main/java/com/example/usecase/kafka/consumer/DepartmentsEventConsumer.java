package com.example.usecase.kafka.consumer;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.departments.DepartmentsEvent;
import com.example.usecase.kafka.processor.DepartmentsEventProcessor;
import com.example.util.MetadataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentsEventConsumer {
    private final ObjectMapper objectMapper;
    private final DepartmentsEventProcessor eventProcessor;

    @KafkaListener(topics = "${spring.kafka.topic.department-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDepartmentEvents(ConsumerRecord<String, String> record, Acknowledgment ack, HttpServletRequest servletRequest) {
        try {
            DepartmentsEvent event = objectMapper.readValue(record.value(), DepartmentsEvent.class);
            log.info("Received department event: {} for code: {}",
                    event.getEventType(), event.getData().getCode());

            Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
            processEvent(event, metadata);
            ack.acknowledge();

            log.info("Successfully processed department event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Error processing department event - Topic: {}, Key: {}, Error: {}",
                    record.topic(), record.key(), e.getMessage());
        }
    }

    private void processEvent(DepartmentsEvent event, Metadata metadata) {
        switch (event.getEventType()) {
            case DEPARTMENT_CREATED:
                eventProcessor.handleDepartmentsCreated(event, metadata);
                break;
            case DEPARTMENT_UPDATED:
                eventProcessor.handleDepartmentsUpdated(event, metadata);
                break;
            case DEPARTMENT_FETCHED:
                eventProcessor.handleDepartmentsFetched(event, metadata);
                break;
            case DEPARTMENT_DELETED:
                eventProcessor.handleDepartmentsDeleted(event, metadata);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
