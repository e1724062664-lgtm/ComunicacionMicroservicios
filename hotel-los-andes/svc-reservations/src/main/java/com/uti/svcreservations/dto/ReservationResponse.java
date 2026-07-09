package com.uti.svcreservations.dto;

import com.uti.svcreservations.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long roomId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private Integer totalNights;
    private LocalDateTime createdAt;

    // Datos enriquecidos, obtenidos de svc-rooms en tiempo real
    private String roomNumber;
    private String roomType;
    private Double pricePerNight;
}
