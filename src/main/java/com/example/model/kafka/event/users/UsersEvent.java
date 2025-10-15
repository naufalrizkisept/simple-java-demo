package com.example.model.kafka.event.users;

import com.example.model.kafka.data.UsersData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEvent {
    private String eventId;
    private UsersEventType eventType;
    private UsersData data;
    private LocalDateTime timestamp;
    private String source;
    private String triggeredBy;
}
