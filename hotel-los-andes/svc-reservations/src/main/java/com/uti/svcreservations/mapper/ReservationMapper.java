package com.uti.svcreservations.mapper;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.model.Reservation;
import org.springframework.stereotype.Component;


@Component
public class ReservationMapper {


    public Reservation toEntity(ReservationRequest request) {
        return Reservation.builder()
                .roomId(request.getRoomId())
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .build();
    }

    // Reservacion, sin datos de la habitacion
    public ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoomId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .status(reservation.getStatus())
                .totalNights(reservation.getTotalNights())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // Reservacion con datos de svc-rooms
    public ReservationResponse toResponseWithRoom(Reservation reservation, RoomResponse roomResponse) {
        ReservationResponse response = toResponse(reservation);
        if (roomResponse != null) {
            response.setRoomNumber(roomResponse.getRoomNumber());
            response.setRoomType(roomResponse.getType());
            response.setPricePerNight(roomResponse.getPricePerNight());
        }
        return response;
    }
}
