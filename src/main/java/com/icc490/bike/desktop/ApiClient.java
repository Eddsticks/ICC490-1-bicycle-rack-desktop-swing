package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordRequest;
import com.icc490.bike.desktop.model.RecordPageResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CompletableFuture<List<Record>> getAllRecords() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/records"))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        RecordPageResponse response = objectMapper.readValue(json, RecordPageResponse.class);
                        return response.getRecords(); // Obtener la lista de records
                    } catch (Exception e) {
                        System.err.println("Error al deserializar la respuesta de registros: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Error al obtener todos los registros de la API: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<Record> createRecord(RecordRequest recordRequest) {
        try {
            String requestBody = objectMapper.writeValueAsString(recordRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(json -> {
                        try {
                            return objectMapper.readValue(json, Record.class);
                        } catch (Exception e) {
                            System.err.println("Error al deserializar el registro creado: " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Error al crear registro en la API: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Error al serializar el RecordRequest o construir la petición: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }
}