package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Obtiene todos los registros de bicicletas de la API.
     * Corresponde al endpoint GET /records
     * @return Un CompletableFuture que contendrá una lista de objetos Record.
     */
    public CompletableFuture<List<?>> getAllRecords() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/records"))
                .GET() // Método HTTP GET
                .header("Accept", "application/json") // Indica que esperamos una respuesta JSON
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        return Arrays.asList(objectMapper.readValue(json, Record[].class));
                    } catch (Exception e) {
                        System.err.println("Error al deserializar la lista de registros: " + e.getMessage());
                        e.printStackTrace();
                        return List.of();
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Error al obtener registros de la API: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                });
    }

    /**
     * Crea un nuevo registro de bicicleta en la API.
     * Corresponde al endpoint POST /records
     * @param recordRequest El objeto RecordRequest con los datos del nuevo registro.
     * @return Un CompletableFuture que contendrá el objeto Record creado (con ID y checkIn).
     */
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
