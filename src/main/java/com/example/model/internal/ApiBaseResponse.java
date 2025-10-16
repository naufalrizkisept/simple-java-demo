package com.example.model.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiBaseResponse <T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 8868723364524612580L;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_message")
    private String responseMessage;

    private T data;
}
