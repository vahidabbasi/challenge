package com.celonis.challenge.controllers;
import static org.springframework.http.ResponseEntity.status;


import com.celonis.challenge.exceptions.*;
import com.celonis.challenge.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception exception) {
        log.error("RuntimeException: ", exception);
        return internalServerError(exception.getMessage());
    }

    @ExceptionHandler(TaskExecutionException.class)
    public ResponseEntity handleHousesManagementException(TaskExecutionException exception) {
        log.error("taskExecutionException: {}", exception.getMessage());
        return createError(exception.getHttpStatus(), exception.getDisplayMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("HttpMessageNotReadableException: ", exception);
        return badRequest("Format not supported");
    }

    /**
     * Returns an INTERNAL_SERVER_ERROR to the client with the given error message.
     */
    private static ResponseEntity<ErrorResponse> internalServerError(String displayMessage) {
        log.error("Internal server error: {}", displayMessage);
        return createError(HttpStatus.SERVICE_UNAVAILABLE, displayMessage);
    }

    /**
     * Returns an HTTP error with the given statuses.
     */
    private static ResponseEntity<ErrorResponse> createError(HttpStatus httpStatus, String message) {
        return status(httpStatus).body(ErrorResponse.builder()
                .message(message)
                .build());
    }

    /**
     * Returns a 400 BAD_REQUEST response with the specified status.
     */
    private ResponseEntity<ErrorResponse> badRequest(String message) {
        return createError(HttpStatus.BAD_REQUEST, message);
    }
}