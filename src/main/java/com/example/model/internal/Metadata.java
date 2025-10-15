package com.example.model.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata implements Serializable {
    private static final long serialVersionUID = -7601414620897757627L;

    private String ipAddress;
    private String userAgent;
    private String userRequest;
    private String channel;
    private String appVersion;
    private String timestamp;
    private String requestId;
    private String signature;
    private String cid;
}
