package com.example.usecase.kafka.processor;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.units.UnitsEvent;
import com.example.model.kafka.event.units.UnitsEventType;
import com.example.model.kibana.KibanaRequest;
import com.example.usecase.helper.KibanaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitsEventProcessor {
    private final KibanaService kibanaService;

    public void handleUnitsCreated(UnitsEvent event, Metadata metadata) {
        log.info("Processing UNIT_CREATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UnitsEventType.UNIT_CREATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUnitsUpdated(UnitsEvent event, Metadata metadata) {
        log.info("Processing UNIT_UPDATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UnitsEventType.UNIT_UPDATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUnitsFetched(UnitsEvent event, Metadata metadata) {
        log.info("Processing UNIT_FETCHED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UnitsEventType.UNIT_FETCHED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleUnitsDeleted(UnitsEvent event, Metadata metadata) {
        log.info("Processing UNIT_DELETED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(UnitsEventType.UNIT_DELETED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }
}
