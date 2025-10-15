package com.example.exception;

import com.example.model.kibana.KibanaRequest;
import com.example.model.kibana.LogKibanaRequest;
import com.example.util.LogUtil;
import com.example.util.MappingUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayloadControllerFilter extends OncePerRequestFilter {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            this.doFilterWrapped(LogUtil.wrapRequest(request), LogUtil.wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper httpServletRequest, ContentCachingResponseWrapper httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        long duration = 0;
        try {
            long startTime = System.currentTimeMillis();
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            duration = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            log.error("Error when do filter wrapped");
        } finally {
            String payloadRequest = LogUtil.logRequest(httpServletRequest.getContentAsByteArray());
            String payloadResponse = LogUtil.logResponse(httpServletResponse);
            LogUtil.logging(httpServletRequest, httpServletResponse, payloadRequest, payloadResponse, duration);

            httpServletResponse.copyBodyToResponse();
        }
    }
}
