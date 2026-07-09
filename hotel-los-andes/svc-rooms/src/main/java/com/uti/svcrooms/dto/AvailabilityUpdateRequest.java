package com.uti.svcrooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {

    @Min(value = 0, message = "Las habitaciones disponibles deben ser mayor o igual a cero")
    private Integer availableRooms;
}
