package com.example.usecase.kafka.processor;

import com.example.model.internal.Metadata;
import com.example.model.kafka.event.departments.DepartmentsEvent;
import com.example.model.kafka.event.departments.DepartmentsEventType;
import com.example.model.kibana.KibanaRequest;
import com.example.usecase.helper.KibanaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentsEventProcessor {
    private final KibanaService kibanaService;
    public void handleDepartmentsCreated(DepartmentsEvent event, Metadata metadata) {
        log.info("Processing DEPARTMENT_CREATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(DepartmentsEventType.DEPARTMENT_CREATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleDepartmentsUpdated(DepartmentsEvent event, Metadata metadata) {
        log.info("Processing DEPARTMENT_UPDATED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(DepartmentsEventType.DEPARTMENT_UPDATED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleDepartmentsFetched(DepartmentsEvent event, Metadata metadata) {
        log.info("Processing DEPARTMENT_FETCHED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(DepartmentsEventType.DEPARTMENT_FETCHED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }

    public void handleDepartmentsDeleted(DepartmentsEvent event, Metadata metadata) {
        log.info("Processing DEPARTMENT_DELETED event for: {} (ID: {})",
                event.getData().getName(), event.getData().getId());

        KibanaRequest request = KibanaRequest.builder()
                .method(DepartmentsEventType.DEPARTMENT_DELETED.toString())
                .channel(metadata.getChannel())
                .host(metadata.getIpAddress())
                .requestId(metadata.getRequestId())
                .requestAt(metadata.getTimestamp())
                .build();

        kibanaService.pushLogToKibana(request);
    }
}
