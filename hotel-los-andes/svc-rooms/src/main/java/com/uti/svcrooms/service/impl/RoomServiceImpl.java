package com.uti.svcrooms.service.impl;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.AvailabilityUpdateRequest;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;
import com.uti.svcrooms.exception.BusinessRuleException;
import com.uti.svcrooms.exception.DuplicateResourceException;
import com.uti.svcrooms.exception.ResourceNotFoundException;
import com.uti.svcrooms.mapper.RoomMapper;
import com.uti.svcrooms.model.Room;
import com.uti.svcrooms.repository.RoomRepository;
import com.uti.svcrooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        log.info("Obteniendo todas las habitaciones registradas");
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        log.info("Obteniendo habitacion con id: {}", id);
        Room room = findRoomOrThrow(id);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        log.info("Creando nueva habitacion con roomNumber: {}", request.getRoomNumber());

        // Regla: roomNumber unico -> 409 Conflict
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException(
                    "Ya existe una habitacion con el numero: " + request.getRoomNumber());
        }

        validateAvailability(request.getAvailableRooms(), request.getTotalCapacity());

        Room room = roomMapper.toEntity(request);
        Room savedRoom = roomRepository.save(room);
        log.info("Habitacion creada exitosamente con id: {}", savedRoom.getId());
        return roomMapper.toResponse(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long id) {
        log.info("Consultando disponibilidad de la habitacion con id: {}", id);
        Room room = findRoomOrThrow(id);
        return roomMapper.toAvailabilityResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse updateAvailability(Long id, AvailabilityUpdateRequest request) {
        log.info("Actualizando disponibilidad de la habitacion con id: {} - nuevo valor: {}",
                id, request.getAvailableRooms());
        Room room = findRoomOrThrow(id);
        validateAvailability(request.getAvailableRooms(), room.getTotalCapacity());
        room.setAvailableRooms(request.getAvailableRooms());
        Room updatedRoom = roomRepository.save(room);
        log.info("Disponibilidad actualizada exitosamente para la habitacion con id: {}", id);
        return roomMapper.toResponse(updatedRoom);
    }

    // Metodo auxiliar que centraliza la busqueda por id y lanza 404 si no existe
    private Room findRoomOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con id: " + id));
    }

    // Valida que availableRooms >= 0 y availableRooms <= totalCapacity -> 422
    private void validateAvailability(Integer availableRooms, Integer totalCapacity) {
        if (availableRooms < 0) {
            throw new BusinessRuleException("Las habitaciones disponibles no pueden ser negativas");
        }
        if (availableRooms > totalCapacity) {
            throw new BusinessRuleException(
                    "Las habitaciones disponibles (" + availableRooms +
                            ") no pueden exceder la capacidad total (" + totalCapacity + ")");
        }
    }
}
