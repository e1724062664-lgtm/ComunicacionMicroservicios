package com.uti.svcreservations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "El id de la habitacion es requerido")
    @Min(value = 1, message = "El id de la habitacion debe ser mayor a 0")
    private Long roomId;

    @NotBlank(message = "El nombre del huesped es requerido")
    private String guestName;

    @NotBlank(message = "El correo del huesped es requerido")
    @Email(message = "El correo del huesped debe ser valido")
    private String guestEmail;

    @NotNull(message = "La fecha de check-in es requerida")
    @FutureOrPresent(message = "La fecha de check-in debe ser hoy o en el futuro")
    private LocalDate checkInDate;

    @NotNull(message = "La fecha de check-out es requerida")
    private LocalDate checkOutDate;
}
