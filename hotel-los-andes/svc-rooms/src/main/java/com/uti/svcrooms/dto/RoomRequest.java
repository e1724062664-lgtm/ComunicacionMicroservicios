package com.uti.svcrooms.dto;

import com.uti.svcrooms.model.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "El numero de habitacion es requerido")
    @Size(max = 20, message = "El numero de habitacion no puede exceder 20 caracteres")
    private String roomNumber;

    @NotNull(message = "El tipo de habitacion es requerido")
    private RoomType type;

    @NotNull(message = "El precio por noche es requerido")
    @Min(value = 0, message = "El precio por noche debe ser mayor o igual a cero")
    private Double pricePerNight;

    @NotNull(message = "La capacidad total es requerida")
    @Min(value = 1, message = "La capacidad total debe ser al menos 1")
    private Integer totalCapacity;

    @NotNull(message = "Las habitaciones disponibles son requeridas")
    //@Min(value = 0, message = "Las habitaciones disponibles deben ser mayor o igual a cero")
    private Integer availableRooms;

    @NotNull(message = "El piso es requerido")
    @Min(value = 0, message = "El piso debe ser mayor o igual a cero")
    private Integer floor;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    private String description;
}
