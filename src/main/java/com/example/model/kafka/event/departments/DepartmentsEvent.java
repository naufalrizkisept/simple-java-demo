package com.example.model.kafka.event.departments;

import com.example.model.kafka.data.DepartmentsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentsEvent {
    private String eventId;
    private DepartmentsEventType eventType;
    private DepartmentsData data;
    private LocalDateTime timestamp;
    private String source;
    private String triggeredBy;
}