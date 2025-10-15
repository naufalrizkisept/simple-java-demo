package com.example.usecase.helper;

import com.example.model.kibana.KibanaRequest;
import com.example.model.kibana.KibanaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KibanaService {
    private final RestTemplate restTemplate;

    @Value("${kibana.url}")
    private String kibanaUrl;

    @Async
    public void pushLogToKibana(KibanaRequest request) {
        log.info("Start push log to kibana with request: " + request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> entity = new HttpEntity<>(request, httpHeaders);

        restTemplate.postForEntity(kibanaUrl, entity, KibanaResponse.class);
    }
}
