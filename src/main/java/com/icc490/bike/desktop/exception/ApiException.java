package com.icc490.bike.desktop.exception;

/**
 * Excepción personalizada para errores específicos de la API.
 */
public class ApiException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ApiException(String message, ApiErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public ApiException(String message, Throwable cause, ApiErrorResponse errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }

    public ApiErrorResponse getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        // Mejoramos el mensaje para que sea más informativo
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (errorResponse != null) {
            sb.append(" [Status: ").append(errorResponse.getStatus());
            if (errorResponse.getError() != null) {
                sb.append(", Error: ").append(errorResponse.getError().toString());
            }
            sb.append("]");
        }
        return sb.toString();
    }
}