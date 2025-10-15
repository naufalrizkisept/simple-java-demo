package com.example.model.kibana;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogKibanaRequest {
    private String payloadRequest;
    private String payloadResponse;
}
