package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LogUtil {
    private LogUtil() {}

    private static final String DOUBLE_LINE = "================================================================================";
    private static final String REQUEST_LINE = "----------------------------------- REQUEST ------------------------------------";
    private static final String RESPONSE_LINE = "----------------------------------- RESPONSE -----------------------------------";

    public static String logRequestHeader(ContentCachingRequestWrapper request) {
        List<String> list = Collections.list(request.getHeaderNames());
        Map<Object, Object> map = list.stream().collect(Collectors.toMap(
                String::valueOf,
                s -> String.valueOf(s).equals("authorization") || String.valueOf(s).equals("key") ? "*************************" : request.getHeader(String.valueOf(s))
        ));
        return JsonUtil.objectAsStringJson(map);
    }

    public static String logRequest(byte[] body) {return new String(body, StandardCharsets.UTF_8);}

    public static String logResponse(ContentCachingResponseWrapper response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    public static String logResponseHeader(ContentCachingResponseWrapper response) {
        List<String> list = new ArrayList<>(response.getHeaderNames().stream().filter(l ->
                !l.equalsIgnoreCase("Vary")).toList());
        Map<Object, Object> map = list.stream().collect(Collectors.toMap(
                String::valueOf,
                s -> String.valueOf(s).equals("authorization") || String.valueOf(s).equals("key") ? "*************************" : response.getHeader(String.valueOf(s))
        ));
        return JsonUtil.objectAsStringJson(map);
    }

    public static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        return request instanceof ContentCachingRequestWrapper ? (ContentCachingRequestWrapper) request : new ContentCachingRequestWrapper(request);
    }

    public static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        return response instanceof ContentCachingResponseWrapper ? (ContentCachingResponseWrapper) response : new ContentCachingResponseWrapper(response);
    }

    public static void logging(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper,
                               String payloadRequest, String payloadResponse) {
        StringBuilder builder = new StringBuilder(StringUtils.LF);
        String uriPath = requestWrapper.getRequestURI();
        String queryString = requestWrapper.getQueryString();
        if (queryString !=  null) {
            uriPath += "?" + queryString;
        }

        builder.append(DOUBLE_LINE).append(StringUtils.LF);
        builder.append(String.format("%s : {%s}", requestWrapper.getMethod(), uriPath)).append(StringUtils.LF);
        builder.append(REQUEST_LINE).append(StringUtils.LF);
        builder.append(String.format("Request Header: {%s}", logRequestHeader(requestWrapper))).append(StringUtils.LF);
        builder.append(String.format("Request Body: {%s}", payloadRequest)).append(StringUtils.LF);
        builder.append(RESPONSE_LINE).append(StringUtils.LF);
        builder.append(String.format("Response Header: {%s}", logResponseHeader(responseWrapper))).append(StringUtils.LF);
        builder.append(String.format("Response Body: {%s}", payloadResponse)).append(StringUtils.LF);
        builder.append(DOUBLE_LINE);
        log.info(builder.toString());
    }

    public static void logging(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper,
                               String payloadRequest, String payloadResponse, long duration) {
        StringBuilder builder = new StringBuilder(StringUtils.LF);
        String uriPath = requestWrapper.getRequestURI();
        String queryString = requestWrapper.getQueryString();
        if (queryString != null) {
            uriPath += "?" + queryString;
        }

        builder.append(DOUBLE_LINE).append(StringUtils.LF);
        builder.append(String.format("%s: {%s}", requestWrapper.getMethod(), uriPath)).append(StringUtils.LF);
        builder.append(REQUEST_LINE).append(StringUtils.LF);
        builder.append(String.format("Request Header: {%s}", logRequestHeader(requestWrapper))).append(StringUtils.LF);
        builder.append(String.format("Request Body: {%s}", payloadRequest)).append(StringUtils.LF);
        builder.append(RESPONSE_LINE).append(StringUtils.LF);
        builder.append(String.format("Response Header: {%s}", logResponseHeader(responseWrapper))).append(StringUtils.LF);
        builder.append(String.format("Response Body: {%s}", payloadResponse)).append(StringUtils.LF);
        builder.append(String.format("Duration: {%s}", duration + " ms")).append(StringUtils.LF);
        builder.append(String.format("Response Status: {%s}", responseWrapper.getStatus())).append(StringUtils.LF);
        builder.append(DOUBLE_LINE);
        log.info(builder.toString());
    }
}
