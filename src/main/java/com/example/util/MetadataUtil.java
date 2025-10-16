package com.example.util;

import com.example.model.internal.Metadata;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.header.Headers;

import java.time.Instant;

public class MetadataUtil {
    private MetadataUtil() {}

    public static Metadata constructMetadata(HttpServletRequest httpServletRequest) {
        return Metadata.builder()
                .ipAddress(StringUtils.isEmpty(httpServletRequest.getHeader("X-Forwarded-For")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("X-Forwarded-For"))
                .userAgent(httpServletRequest.getHeader("User-Agent"))
                .userRequest(httpServletRequest.getHeader("user-request"))
                .appVersion(httpServletRequest.getHeader("app_version"))
                .channel(httpServletRequest.getHeader("channel"))
                .signature(httpServletRequest.getHeader("Signature"))
                .timestamp(httpServletRequest.getHeader("request-at"))
                .cid(httpServletRequest.getHeader("cid"))
                .requestId(httpServletRequest.getHeader("request-id"))
                .build();
    }

    public static Metadata extractMetadataFromKafkaHeaders(Headers headers) {
        return Metadata.builder()
                .timestamp(Instant.now().toString())
                .userAgent(headers.lastHeader("User-Agent") != null ? new String(headers.lastHeader("User-Agent").value()) : null)
                .userRequest(headers.lastHeader("user-request") != null ? new String(headers.lastHeader("user-request").value()) : null)
                .appVersion(headers.lastHeader("app_version") != null ? new String(headers.lastHeader("app_version").value()) : null)
                .channel(headers.lastHeader("channel") != null ? new String(headers.lastHeader("channel").value()) : null)
                .signature(headers.lastHeader("signature") != null ? new String(headers.lastHeader("signature").value()) : null)
                .timestamp(Instant.now().toString())
                .cid(headers.lastHeader("cid") != null ? new String(headers.lastHeader("cid").value()) : null)
                .requestId(headers.lastHeader("request-id") != null ? new String(headers.lastHeader("request-id").value()) : null)
                .build();
    }
}
