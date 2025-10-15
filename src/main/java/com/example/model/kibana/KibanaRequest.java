package com.example.model.kibana;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KibanaRequest {
    private String method;
    private String channel;
    private String host;
    private String requestId;
    private String requestAt;
}
