package com.example.util;

import com.example.model.internal.Metadata;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

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
}
