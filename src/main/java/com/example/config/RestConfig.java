package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Value(value = "${rest.client.connect.timeout}")
    private Long restClientConnectTimeout;

    @Value(value = "${rest.client.read.timeout}")
    private Long restClientReadTimeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(restClientConnectTimeout.intValue());
        requestFactory.setConnectTimeout(restClientReadTimeout.intValue());
        return new RestTemplate(requestFactory);
    }
}
