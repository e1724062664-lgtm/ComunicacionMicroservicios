package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.ResourceNotFoundException;
import com.uti.svcreservations.exception.RoomServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
public class RoomRestTemplateClient {

    private final RestTemplate restTemplate;

    @Value("${rooms.service.url}")
    private String roomsServiceUrl;

    public RoomRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Consulta la disponibilidad de una habitacion
    public RoomAvailabilityResponse getRoomAvailability(Long roomId) {
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId + "/availability";
        log.info("RestTemplate - Llamando a svc-rooms: GET {}", url);
        try {
            ResponseEntity<RoomAvailabilityResponse> response = restTemplate.getForEntity(
                    url, RoomAvailabilityResponse.class);
            log.info("RestTemplate - Estado de la respuesta: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Habitacion no encontrada en svc-rooms con id: " + roomId);
            }
            throw new RoomServiceException("Error de cliente al llamar a svc-rooms: " + ex.getMessage());
        } catch (ResourceAccessException ex) {
            log.error("RestTemplate - No se logro conectar con svc-rooms: {}", ex.getMessage());
            throw new RoomServiceException("No se logro conectar con svc-rooms: " + ex.getMessage(), ex);
        }
    }

    // Consulta los datos completos de una habitacion
    public RoomResponse getRoomById(Long roomId) {
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId;
        log.info("RestTemplate - Llamando a svc-rooms: GET {}", url);
        try {
            ResponseEntity<RoomResponse> response = restTemplate.getForEntity(url, RoomResponse.class);
            log.info("RestTemplate - Estado de la respuesta: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Habitacion no encontrada en svc-rooms con id: " + roomId);
            }
            throw new RoomServiceException("Error de cliente al llamar a svc-rooms: " + ex.getMessage());
        } catch (ResourceAccessException ex) {
            log.error("RestTemplate - No se logro conectar con svc-rooms: {}", ex.getMessage());
            throw new RoomServiceException("No se logro conectar con svc-rooms: " + ex.getMessage(), ex);
        }
    }
}
