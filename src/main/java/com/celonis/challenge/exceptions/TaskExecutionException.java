package com.celonis.challenge.exceptions;

import org.springframework.http.HttpStatus;

public class TaskExecutionException extends RuntimeException{
    private String displayMessage;
    private HttpStatus httpStatus;

    public TaskExecutionException(String message,  Throwable cause) {
        super(message, cause);
        displayMessage = message;
    }

    public TaskExecutionException(String message,
                                     HttpStatus httpStatus) {
        super(message);
        displayMessage = message;
        this.httpStatus = httpStatus;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
