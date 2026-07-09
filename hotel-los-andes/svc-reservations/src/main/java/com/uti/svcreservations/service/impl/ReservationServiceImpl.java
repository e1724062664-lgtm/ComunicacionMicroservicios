package com.uti.svcreservations.service.impl;

import com.uti.svcreservations.client.RoomRestTemplateClient;
import com.uti.svcreservations.client.RoomWebClient;
import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.BusinessRuleException;
import com.uti.svcreservations.exception.DuplicateReservationException;
import com.uti.svcreservations.exception.InvalidDateRangeException;
import com.uti.svcreservations.exception.ResourceNotFoundException;
import com.uti.svcreservations.exception.RoomServiceException;
import com.uti.svcreservations.mapper.ReservationMapper;
import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.ReservationStatus;
import com.uti.svcreservations.repository.ReservationRepository;
import com.uti.svcreservations.service.ReservationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacion de la logica de negocio para la gestion de reservas.
 * Se comunica con svc-rooms via RestTemplate y WebClient, protegida
 * por Resilience4j (Circuit Breaker + Retry) con fallback ante fallas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private static final String FALLBACK_MESSAGE = "Room information temporarily unavailable";

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomRestTemplateClient roomRestTemplateClient;
    private final RoomWebClient roomWebClient;

    @Override
    @Transactional
    @CircuitBreaker(name = "roomsService", fallbackMethod = "createReservationFallback")
    @Retry(name = "roomsService")
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creando reserva para la habitacion id: {} - huesped: {}",
                request.getRoomId(), request.getGuestEmail());

        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        // Regla: no permitir dos reservas ACTIVE del mismo huesped para la misma habitacion
        if (reservationRepository.existsActiveReservationForGuestAndRoom(request.getRoomId(), request.getGuestEmail())) {
            throw new DuplicateReservationException(
                    "El huesped " + request.getGuestEmail() +
                            " ya tiene una reserva ACTIVE para la habitacion con id: " + request.getRoomId());
        }

        // 1) Verifica disponibilidad en svc-rooms via RestTemplate
        log.info("Verificando disponibilidad de la habitacion via RestTemplate...");
        RoomAvailabilityResponse availability = roomRestTemplateClient.getRoomAvailability(request.getRoomId());

        if (!availability.isAvailable()) {
            throw new BusinessRuleException("Room is not available");
        }

        // 2) Persiste la reserva (totalNights se calcula automaticamente en @PrePersist)
        Reservation reservation = reservationMapper.toEntity(request);
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reserva creada exitosamente con id: {}", savedReservation.getId());

        // 3) Obtiene los detalles de la habitacion via WebClient para enriquecer la respuesta
        log.info("Obteniendo detalles de la habitacion via WebClient...");
        RoomResponse roomResponse = roomWebClient.getRoomById(request.getRoomId());
        return reservationMapper.toResponseWithRoom(savedReservation, roomResponse);
    }

    // Fallback de createReservation: si la falla es de negocio o de validacion, se propaga tal cual;
    // si es de comunicacion con svc-rooms, se traduce a RoomServiceException (503)
    public ReservationResponse createReservationFallback(ReservationRequest request, Throwable throwable) {
        log.warn("Circuit Breaker ACTIVADO en createReservation - Razon: {}", throwable.getMessage());
        if (throwable instanceof BusinessRuleException || throwable instanceof DuplicateReservationException
                || throwable instanceof ResourceNotFoundException || throwable instanceof InvalidDateRangeException) {
            throw (RuntimeException) throwable;
        }
        throw new RoomServiceException(FALLBACK_MESSAGE + ". Razon: " + throwable.getMessage());
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "roomsService", fallbackMethod = "getReservationByIdFallback")
    @Retry(name = "roomsService")
    public ReservationResponse getReservationById(Long id) {
        log.info("Obteniendo reserva con id: {}", id);
        Reservation reservation = findReservationOrThrow(id);
        RoomResponse roomResponse = roomRestTemplateClient.getRoomById(reservation.getRoomId());
        return reservationMapper.toResponseWithRoom(reservation, roomResponse);
    }

    // Fallback de getReservationById: retorna la reserva local con datos de habitacion
    // marcados como temporalmente no disponibles (svc-rooms caido / circuito abierto)
    public ReservationResponse getReservationByIdFallback(Long id, Throwable throwable) {
        log.warn("Circuit Breaker ACTIVADO en getReservationById - Razon: {}", throwable.getMessage());
        if (throwable instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) throwable;
        }
        Reservation reservation = findReservationOrThrow(id);
        ReservationResponse response = reservationMapper.toResponse(reservation);
        response.setRoomNumber(FALLBACK_MESSAGE);
        response.setRoomType("N/A");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByGuestEmail(String guestEmail) {
        log.info("Obteniendo todas las reservas del huesped: {}", guestEmail);
        return reservationRepository.findByGuestEmail(guestEmail)
                .stream()
                .map(this::enrichWithRoomDataSafely)
                .collect(Collectors.toList());
    }

    // Enriquece cada reserva con datos de svc-rooms via WebClient;
    // si el servicio no responde, degrada de forma controlada sin romper el listado
    private ReservationResponse enrichWithRoomDataSafely(Reservation reservation) {
        try {
            RoomResponse roomResponse = roomWebClient.getRoomById(reservation.getRoomId());
            return reservationMapper.toResponseWithRoom(reservation, roomResponse);
        } catch (Exception ex) {
            log.warn("No se logro obtener el detalle de la habitacion para la reserva con id: {}", reservation.getId());
            ReservationResponse response = reservationMapper.toResponse(reservation);
            response.setRoomNumber(FALLBACK_MESSAGE);
            return response;
        }
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "roomsService", fallbackMethod = "checkoutFallback")
    @Retry(name = "roomsService")
    public ReservationResponse checkout(Long id) {
        log.info("Procesando checkout para la reserva con id: {}", id);
        Reservation reservation = findReservationOrThrow(id);

        // Regla: checkout solo para reservas ACTIVE
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "La reserva con id: " + id + " no puede ser procesada. Estado actual: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Checkout procesado exitosamente para la reserva con id: {}", id);

        RoomResponse roomResponse = roomRestTemplateClient.getRoomById(updatedReservation.getRoomId());
        return reservationMapper.toResponseWithRoom(updatedReservation, roomResponse);
    }

    // Fallback de checkout: el estado ya quedo persistido; solo se degrada
    // la informacion de la habitacion si svc-rooms no responde
    public ReservationResponse checkoutFallback(Long id, Throwable throwable) {
        log.warn("Circuit Breaker ACTIVADO en checkout - Razon: {}", throwable.getMessage());
        if (throwable instanceof BusinessRuleException || throwable instanceof ResourceNotFoundException) {
            throw (RuntimeException) throwable;
        }
        Reservation reservation = findReservationOrThrow(id);
        ReservationResponse response = reservationMapper.toResponse(reservation);
        response.setRoomNumber(FALLBACK_MESSAGE);
        response.setRoomType("N/A");
        return response;
    }

    // Metodo auxiliar que centraliza la busqueda por id y lanza 404 si no existe
    private Reservation findReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
    }

    // Valida que checkOutDate > checkInDate -> 400 Bad Request
    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidDateRangeException("La fecha de check-out debe ser posterior a la fecha de check-in");
        }
    }
}
