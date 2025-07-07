package com.icc490.bike.desktop.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
