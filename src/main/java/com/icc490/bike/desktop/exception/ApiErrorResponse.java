package com.icc490.bike.desktop.exception;

import java.time.Instant;
import java.util.List;

/**
 * Clase para mapear las respuestas de error de la API.
 */
public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private Object error;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(Instant timestamp, int status, Object error) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
    }

    // Getters
    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public Object getError() {
        return error;
    }

    // Setters
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(Object error) {
        this.error = error;
    }

    @Override
    public String toString() {
        String errorContent;
        if (error instanceof List) {
            errorContent = "[\"" + String.join("\", \"", (List<String>) error) + "\"]";
        } else {
            errorContent = "\"" + error + "\"";
        }
        return "ApiErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error=" + errorContent +
                '}';
    }
}