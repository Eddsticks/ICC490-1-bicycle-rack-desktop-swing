package com.icc490.bike.desktop.exception;

public class ApiErrorResponse extends RuntimeException {
    public ApiErrorResponse(String message) {
        super(message);
    }
}
