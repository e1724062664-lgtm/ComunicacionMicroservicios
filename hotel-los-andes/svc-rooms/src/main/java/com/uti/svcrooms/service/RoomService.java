package com.uti.svcrooms.service;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.AvailabilityUpdateRequest;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;

import java.util.List;


public interface RoomService {

    // Retorna todas las habitaciones registradas
    List<RoomResponse> getAllRooms();

    // Retorna una habitacion por id
    RoomResponse getRoomById(Long id);

    // Registra una nueva habitacion
    RoomResponse createRoom(RoomRequest request);

    // Consulta la disponibilidad de una habitacion
    AvailabilityResponse getAvailability(Long id);

    // Actualiza la cantidad de habitaciones disponibles
    RoomResponse updateAvailability(Long id, AvailabilityUpdateRequest request);
}
