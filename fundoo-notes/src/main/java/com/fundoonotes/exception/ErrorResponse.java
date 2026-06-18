package com.fundoonotes.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private int statusCode;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int statusCode, LocalDateTime timestamp) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }


    public String getMessage() { return message; }
    public int getStatusCode() { return statusCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
