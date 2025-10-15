package com.example.usecase.kafka.producer;

import com.example.model.db.UnitsDao;
import com.example.model.db.UsersDao;
import com.example.model.kafka.data.UsersData;
import com.example.model.kafka.event.units.UnitsEvent;
import com.example.model.kafka.event.units.UnitsEventType;
import com.example.model.kafka.event.users.UsersEvent;
import com.example.model.kafka.event.users.UsersEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.user-events}")
    private String userTopic;

    public void sendUserCreated(UsersDao user, String triggeredBy) {
        UsersEvent event = buildEvent(user, UsersEventType.USER_CREATED, triggeredBy);
        sendEvent(event, user.getEmail());
    }

    public void sendUserUpdated(UsersDao user, String triggeredBy) {
        UsersEvent event = buildEvent(user, UsersEventType.USER_UPDATED, triggeredBy);
        sendEvent(event, user.getEmail());
    }

    public void sendUserFetched(UsersDao user, String triggeredBy) {
        UsersEvent event = buildEvent(user, UsersEventType.USER_FETCHED, triggeredBy);
        sendEvent(event, user.getEmail());
    }

    public void sendUserDeleted(UsersDao user, String triggeredBy) {
        UsersEvent event = buildEvent(user, UsersEventType.USER_DELETED, triggeredBy);
        sendEvent(event, user.getEmail());
    }

    private UsersEvent buildEvent(UsersDao user, UsersEventType eventType, String triggeredBy) {
        UsersData data = UsersData.builder()
                .id(user.getId())
                .departmentId(user.getDepartmentId())
                .unitId(user.getUnitId())
                .name(user.getName())
                .email(user.getEmail())
                .isActive(user.isActive())
                .isDeleted(user.isDeleted())
                .sendMail(user.isSendMail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return UsersEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .data(data)
                .timestamp(LocalDateTime.now())
                .source("units-service")
                .triggeredBy(triggeredBy)
                .build();
    }

    private void sendEvent(UsersEvent event, String key) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userTopic, key, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent {} event for user: {}, offset: {}",
                                    event.getEventType(), key, result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send {} event for user: {}, error: {}",
                                    event.getEventType(), key, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing unit event for: {}, error: {}", key, e.getMessage());
        }
    }
}
