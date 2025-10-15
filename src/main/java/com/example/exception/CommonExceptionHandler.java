package com.example.exception;

import com.example.model.internal.ApiBaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.Serializable;

import static com.example.constant.GeneralConstant.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception e) {
        log.error("Exception is CAUGHT. Details: ", e);
        ApiBaseResponse<Serializable> result = new ApiBaseResponse<>();
        result.setResponseCode(STATUS_CODE_SYSTEM_ERROR);
        result.setResponseMessage(ERROR_SYSTEM_ERROR);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({UniqueException.class})
    public ResponseEntity<Object> uniqueException(UniqueException e) {
        ApiBaseResponse<Serializable> response = e.apiBaseResponse();

        HttpStatus httpStatus = HttpStatus.OK;

        if (response.getResponseCode().equalsIgnoreCase(STATUS_CODE_BAD_REQUEST)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (response.getResponseCode().equalsIgnoreCase(STATUS_CODE_SYSTEM_ERROR)) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
}
