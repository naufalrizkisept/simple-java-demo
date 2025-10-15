package com.example.util;

import com.example.model.internal.ApiBaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.constant.GeneralConstant.*;

public class ResponseUtil {
    private ResponseUtil() {}

    private static final Map<String, HttpStatus> STATUS_CODE_HTTP_MAP = new HashMap<>();
    static {
        STATUS_CODE_HTTP_MAP.put(STATUS_CODE_SUCCESS, HttpStatus.OK);
        STATUS_CODE_HTTP_MAP.put(STATUS_CODE_BAD_REQUEST, HttpStatus.BAD_REQUEST);
        STATUS_CODE_HTTP_MAP.put(STATUS_CODE_SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T extends Serializable> ApiBaseResponse<T> buildResponse(String code, String message, T data) {
        ApiBaseResponse<T> result = new ApiBaseResponse<>();
        result.setResponseCode(code);
        result.setResponseMessage(message);
        result.setData(data);

        return result;
    }

    public static <T extends Serializable> ApiBaseResponse<T> buildResponse(String code, String message) {
        ApiBaseResponse<T> result = new ApiBaseResponse<>();
        result.setResponseCode(code);
        result.setResponseMessage(message);

        return result;
    }

    public static HttpStatus errorCodeToHttpStatus(String errorCode) {
        return Optional.ofNullable(STATUS_CODE_HTTP_MAP.get(errorCode)).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T extends Serializable> ResponseEntity<ApiBaseResponse<T>> buildHttpResponse(ApiBaseResponse<T> result) {
        return new ResponseEntity<>(
                result, errorCodeToHttpStatus(result.getResponseCode())
        );
    }
}
