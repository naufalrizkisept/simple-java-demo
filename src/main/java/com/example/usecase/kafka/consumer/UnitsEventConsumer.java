package com.example.usecase.kafka.consumer;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.units.UnitsEvent;
import com.example.usecase.kafka.processor.UnitsEventProcessor;
import com.example.util.MetadataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitsEventConsumer {
    private final ObjectMapper objectMapper;
    private final UnitsEventProcessor eventProcessor;

    @KafkaListener(topics = "${spring.kafka.topic.unit-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUnitsEvents(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            UnitsEvent event = objectMapper.readValue(record.value(), UnitsEvent.class);
            log.info("Received unit event: {} for ID: {}",
                    event.getEventType(), event.getData().getId());

            Metadata metadata = MetadataUtil.extractMetadataFromKafkaHeaders(record.headers());
            processEvent(event, metadata);
            ack.acknowledge();

            log.info("Successfully processed unit event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Error processing unit event - Topic: {}, Key: {}, Error: {}",
                    record.topic(), record.key(), e.getMessage());
        }
    }

    private void processEvent(UnitsEvent event, Metadata metadata) {
        switch (event.getEventType()) {
            case UNIT_CREATED:
                eventProcessor.handleUnitsCreated(event, metadata);
                break;
            case UNIT_UPDATED:
                eventProcessor.handleUnitsUpdated(event, metadata);
                break;
            case UNIT_FETCHED:
                eventProcessor.handleUnitsFetched(event, metadata);
                break;
            case UNIT_DELETED:
                eventProcessor.handleUnitsDeleted(event, metadata);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
