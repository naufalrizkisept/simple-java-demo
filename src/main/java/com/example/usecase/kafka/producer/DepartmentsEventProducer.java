package com.example.usecase.kafka.producer;

import com.example.model.db.DepartmentsDao;
import com.example.model.kafka.data.DepartmentsData;
import com.example.model.kafka.event.departments.DepartmentsEvent;
import com.example.model.kafka.event.departments.DepartmentsEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentsEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.department-events}")
    private String departmentTopic;

    public void sendDepartmentCreated(DepartmentsDao department, String triggeredBy) {
        DepartmentsEvent event = buildEvent(department, DepartmentsEventType.DEPARTMENT_CREATED, triggeredBy);
        sendEvent(event, department.getCode());
    }

    public void sendDepartmentUpdated(DepartmentsDao department, String triggeredBy) {
        DepartmentsEvent event = buildEvent(department, DepartmentsEventType.DEPARTMENT_UPDATED, triggeredBy);
        sendEvent(event, department.getCode());
    }

    public void sendDepartmentFetched(DepartmentsDao department, String triggeredBy) {
        DepartmentsEvent event = buildEvent(department, DepartmentsEventType.DEPARTMENT_FETCHED, triggeredBy);
        sendEvent(event, department.getCode());
    }

    public void sendDepartmentDeleted(DepartmentsDao department, String triggeredBy) {
        DepartmentsEvent event = buildEvent(department, DepartmentsEventType.DEPARTMENT_DELETED, triggeredBy);
        sendEvent(event, department.getCode());
    }

    private DepartmentsEvent buildEvent(DepartmentsDao department, DepartmentsEventType eventType, String triggeredBy) {
        DepartmentsData data = DepartmentsData.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .key(department.getKey())
                .build();

        return DepartmentsEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .data(data)
                .source("departments-service")
                .triggeredBy(triggeredBy)
                .build();
    }

    private void sendEvent(DepartmentsEvent event, String key) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(departmentTopic, key, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent {} event for department: {}, offset: {}",
                                    event.getEventType(), key, result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send {} event for department: {}, error: {}",
                                    event.getEventType(), key, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing department event for: {}, error: {}", key, e.getMessage());
        }
    }
}
