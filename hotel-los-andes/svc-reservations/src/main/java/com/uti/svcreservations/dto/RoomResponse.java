package com.uti.svcreservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private Integer totalCapacity;
    private Integer availableRooms;
    private Integer floor;
    private String description;
    private Boolean available;
}
