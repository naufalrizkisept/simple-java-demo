package com.example.usecase.kafka.processor;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.users.UsersEvent;
import com.example.model.kafka.event.users.UsersEventType;
import com.example.model.kibana.KibanaRequest;
import com.example.usecase.helper.KibanaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersEventProcessor {
    private final KibanaService kibanaService;

    public void handleUsersCreated(UsersEvent event, Metadata metadata) {
        log.info("Processing USER_CREATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UsersEventType.USER_CREATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUsersUpdated(UsersEvent event, Metadata metadata) {
        log.info("Processing USER_UPDATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UsersEventType.USER_UPDATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUsersFetched(UsersEvent event, Metadata metadata) {
        log.info("Processing USER_FETCHED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UsersEventType.USER_FETCHED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUsersDeleted(UsersEvent event, Metadata metadata) {
        log.info("Processing USER_DELETED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UsersEventType.USER_DELETED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }
}
