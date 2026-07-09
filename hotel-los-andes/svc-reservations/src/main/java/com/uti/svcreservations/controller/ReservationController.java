package com.uti.svcreservations.controller;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    // Crea una nueva reserva; verifica disponibilidad en svc-rooms

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        log.info("POST /api/v1/reservations - Creando reserva para la habitacion id: {}", request.getRoomId());
        ReservationResponse createdReservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    // Obtiene una reserva por id, enriquecida con los datos de la habitacion
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        log.info("GET /api/v1/reservations/{} - Obteniendo reserva por id", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    // Obtiene todas las reservas de un huesped a partir de su correo
    @GetMapping("/guest/{email}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByGuestEmail(@PathVariable String email) {
        log.info("GET /api/v1/reservations/guest/{} - Obteniendo reservas del huesped", email);
        return ResponseEntity.ok(reservationService.getReservationsByGuestEmail(email));
    }

    // Registra el checkout de una reserva ACTIVE
    @PatchMapping("/{id}/checkout")
    public ResponseEntity<ReservationResponse> checkout(@PathVariable Long id) {
        log.info("PATCH /api/v1/reservations/{}/checkout - Procesando checkout", id);
        return ResponseEntity.ok(reservationService.checkout(id));
    }
}
