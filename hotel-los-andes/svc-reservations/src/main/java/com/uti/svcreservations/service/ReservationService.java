package com.uti.svcreservations.service;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;

import java.util.List;


public interface ReservationService {

    // Crea una nueva reserva
    ReservationResponse createReservation(ReservationRequest request);

    // Obtiene una reserva por id
    ReservationResponse getReservationById(Long id);

    // Obtiene todas las reservas de un huesped por su correo
    List<ReservationResponse> getReservationsByGuestEmail(String guestEmail);

    // Registra el checkout de una reserva ACTIVE
    ReservationResponse checkout(Long id);
}
