package com.uti.svcrooms.controller;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.AvailabilityUpdateRequest;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;
import com.uti.svcrooms.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        log.info("GET /api/v1/rooms - Obteniendo todas las habitaciones");
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{} - Obteniendo habitacion por id", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        log.info("POST /api/v1/rooms - Creando habitacion con roomNumber: {}", request.getRoomNumber());
        RoomResponse createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{}/availability - Consultando disponibilidad", id);
        return ResponseEntity.ok(roomService.getAvailability(id));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<RoomResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateRequest request) {
        log.info("PATCH /api/v1/rooms/{}/availability - Nuevo valor: {}", id, request.getAvailableRooms());
        return ResponseEntity.ok(roomService.updateAvailability(id, request));
    }
}
