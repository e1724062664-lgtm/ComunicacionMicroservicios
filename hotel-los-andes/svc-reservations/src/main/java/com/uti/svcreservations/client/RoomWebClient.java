package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.ResourceNotFoundException;
import com.uti.svcreservations.exception.RoomServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@Slf4j
public class RoomWebClient {

    private final WebClient webClient;

    public RoomWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    // Obtiene los datos completos de una habitacion
    public RoomResponse getRoomById(Long roomId) {
        log.info("WebClient - Llamando a svc-rooms: GET /api/v1/rooms/{}", roomId);
        try {
            return webClient
                    .get()
                    .uri("/api/v1/rooms/{roomId}", roomId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new ResourceNotFoundException(
                                            "Habitacion no encontrada en svc-rooms con id: " + roomId))
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomServiceException(
                                            "Error de cliente desde svc-rooms: " + body))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomServiceException(
                                            "Error del servidor desde svc-rooms: " + body))
                    )
                    .bodyToMono(RoomResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClient - Error HTTP desde svc-rooms: {} {}", ex.getStatusCode(), ex.getMessage());
            throw new RoomServiceException("Error al llamar a svc-rooms: " + ex.getMessage(), ex);
        } catch (ResourceNotFoundException | RoomServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("WebClient - No se logro conectar con svc-rooms: {}", ex.getMessage());
            throw new RoomServiceException("No se logro conectar con svc-rooms: " + ex.getMessage(), ex);
        }
    }
}
