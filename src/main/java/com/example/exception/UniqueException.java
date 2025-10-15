package com.example.exception;

import com.example.model.internal.ApiBaseResponse;
import com.example.util.ResponseUtil;

import java.io.Serializable;

public class UniqueException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public UniqueException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApiBaseResponse<Serializable> apiBaseResponse() {
        return ResponseUtil.buildResponse(this.errorCode, this.errorMessage);
    }
}
