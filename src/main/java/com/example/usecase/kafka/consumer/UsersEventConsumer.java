package com.example.usecase.kafka.consumer;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.users.UsersEvent;
import com.example.usecase.kafka.processor.UsersEventProcessor;
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
public class UsersEventConsumer {
    private final ObjectMapper objectMapper;
    private final UsersEventProcessor eventProcessor;

    @KafkaListener(topics = "${spring.kafka.topic.user-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUsersEvents(ConsumerRecord<String, String> record, Acknowledgment ack, HttpServletRequest servletRequest) {
        try {
            UsersEvent event = objectMapper.readValue(record.value(), UsersEvent.class);
            log.info("Received user event: {} for ID: {}",
                    event.getEventType(), event.getData().getId());

            Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
            processEvent(event, metadata);
            ack.acknowledge();

            log.info("Successfully processed user event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Error processing unit event - Topic: {}, Key: {}, Error: {}",
                    record.topic(), record.key(), e.getMessage());
        }
    }

    private void processEvent(UsersEvent event, Metadata metadata) {
        switch (event.getEventType()) {
            case USER_CREATED:
                eventProcessor.handleUsersCreated(event, metadata);
                break;
            case USER_UPDATED:
                eventProcessor.handleUsersUpdated(event, metadata);
                break;
            case USER_FETCHED:
                eventProcessor.handleUsersFetched(event, metadata);
                break;
            case USER_DELETED:
                eventProcessor.handleUsersDeleted(event, metadata);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
