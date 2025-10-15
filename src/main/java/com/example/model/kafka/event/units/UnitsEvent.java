package com.example.model.kafka.event.units;

import com.example.model.kafka.data.UnitsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitsEvent {
    private String eventId;
    private UnitsEventType eventType;
    private UnitsData data;
    private LocalDateTime timestamp;
    private String source;
    private String triggeredBy;
}
