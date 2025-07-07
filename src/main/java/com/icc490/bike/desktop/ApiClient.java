package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icc490.bike.desktop.exception.ApiErrorResponse;
import com.icc490.bike.desktop.exception.ApiException;
import com.icc490.bike.desktop.model.Rack;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordPageResponse;
import com.icc490.bike.desktop.model.RecordRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Maneja respuestas HTTP, Checkea errores del API.
     */
    private <T> CompletableFuture<T> handleResponse(HttpResponse<String> response, Class<T> valueType) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            try {
                return CompletableFuture.completedFuture(objectMapper.readValue(response.body(), valueType));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(new IOException("Error deserializing response: " + e.getMessage(), e));
            }
        } else {
            try {
                ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
                return CompletableFuture.failedFuture(new ApiException("API Error: " + response.statusCode(), errorResponse));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(new IOException("API Error (" + response.statusCode() + ") but could not parse error response: " + e.getMessage(), e));
            }
        }
    }

    /**
     * Obtiene todos los registros de bicicletas de la API con paginación y filtrado.
     * Corresponde al endpoint GET /records
     * @param pageToken Token para la siguiente página (opcional, null para la primera página).
     * @param maxPageSize Tamaño máximo de la página (opcional, 10 por defecto en la API).
     * @param filter Filtro de búsqueda (opcional, formato "columna operador valor").
     * @return Un CompletableFuture que contendrá un objeto RecordPageResponse.
     */
    public CompletableFuture<RecordPageResponse> getRecords(String pageToken, Integer maxPageSize, String filter) {
        StringBuilder uriBuilder = new StringBuilder(BASE_URL).append("/records");
        uriBuilder.append("?");

        if (pageToken != null && !pageToken.isEmpty()) {
            uriBuilder.append("pageToken=").append(pageToken).append("&");
        }
        if (maxPageSize != null) {
            uriBuilder.append("maxPageSize=").append(maxPageSize).append("&");
        }
        if (filter != null && !filter.isEmpty()) {
            uriBuilder.append("filter=").append(filter).append("&");
        }

        if (uriBuilder.charAt(uriBuilder.length() - 1) == '&' || uriBuilder.charAt(uriBuilder.length() - 1) == '?') {
            uriBuilder.setLength(uriBuilder.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> handleResponse(response, RecordPageResponse.class));
    }

    /**
     * Crea un nuevo registro de bicicleta en la API.
     * Corresponde al endpoint POST /records
     * @param recordRequest Objeto RecordRequest con los datos del nuevo registro.
     * @return Un CompletableFuture que contendrá el objeto Record creado.
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
                    .thenCompose(response -> handleResponse(response, Record.class));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new IOException("Error serializing RecordRequest or building request: " + e.getMessage(), e));
        }
    }

    /**
     * Realiza el check-out de un registro de bicicleta.
     * Corresponde al endpoint PATCH /records/{id}/checkout
     * @param recordId El ID del registro a hacer check-out.
     * @return Un CompletableFuture que contendrá el objeto Record actualizado.
     */
    public CompletableFuture<Record> checkOutRecord(Long recordId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/records/" + recordId + "/checkout"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody()) // PATCH sin cuerpo
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> handleResponse(response, Record.class));
    }

    /**
     * Obtiene la información de un rack por su ID.
     * Corresponde al endpoint GET /racks/{id}
     * @param rackId El ID del rack a buscar.
     * @return Un CompletableFuture que contendrá el objeto Rack.
     */
    public CompletableFuture<Rack> getRackById(Long rackId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/racks/" + rackId))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> handleResponse(response, Rack.class));
    }
}