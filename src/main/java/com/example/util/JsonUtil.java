package com.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
    private JsonUtil() {}

    public static String objectAsStringJson(Object data) {
        try {
            return (new ObjectMapper()).writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error when parsing object to String JSON: ", e);
            return "";
        }
    }
}
