package com.example.usecase.helper;

import com.example.model.kibana.KibanaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreLogListener {
    private final KibanaService kibanaService;

    @Async
    @EventListener
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handlePushToKibana(KibanaRequest request) {
        try {
            kibanaService.pushLogToKibana(request);
        } catch (Exception e) {
            log.error("Error when push log to kibana");
        }
    }
}
