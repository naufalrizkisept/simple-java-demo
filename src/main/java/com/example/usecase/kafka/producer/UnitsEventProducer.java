package com.example.usecase.kafka.producer;

import com.example.model.db.UnitsDao;
import com.example.model.kafka.data.UnitsData;
import com.example.model.kafka.event.units.UnitsEvent;
import com.example.model.kafka.event.units.UnitsEventType;
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
public class UnitsEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.unit-events}")
    private String unitTopic;

    public void sendUnitCreated(UnitsDao unit, String triggeredBy) {
        UnitsEvent event = buildEvent(unit, UnitsEventType.UNIT_CREATED, triggeredBy);
        sendEvent(event, String.valueOf(unit.getId()));
    }

    public void sendUnitUpdated(UnitsDao unit, String triggeredBy) {
        UnitsEvent event = buildEvent(unit, UnitsEventType.UNIT_UPDATED, triggeredBy);
        sendEvent(event, String.valueOf(unit.getId()));
    }

    public void sendUnitFetched(UnitsDao unit, String triggeredBy) {
        UnitsEvent event = buildEvent(unit, UnitsEventType.UNIT_FETCHED, triggeredBy);
        sendEvent(event, String.valueOf(unit.getId()));
    }

    public void sendUnitDeleted(UnitsDao unit, String triggeredBy) {
        UnitsEvent event = buildEvent(unit, UnitsEventType.UNIT_DELETED, triggeredBy);
        sendEvent(event, String.valueOf(unit.getId()));
    }

    private UnitsEvent buildEvent(UnitsDao unit, UnitsEventType eventType, String triggeredBy) {
        UnitsData data = UnitsData.builder()
                .id(unit.getId())
                .departmentId(unit.getDepartmentId())
                .name(unit.getName())
                .address(unit.getAddress())
                .build();

        return UnitsEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .data(data)
                .source("units-service")
                .triggeredBy(triggeredBy)
                .build();
    }

    private void sendEvent(UnitsEvent event, String key) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(unitTopic, key, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent {} event for unit: {}, offset: {}",
                                    event.getEventType(), key, result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send {} event for unit: {}, error: {}",
                                    event.getEventType(), key, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing unit event for: {}, error: {}", key, e.getMessage());
        }
    }
}
