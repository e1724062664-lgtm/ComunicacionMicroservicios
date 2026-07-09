package com.uti.svcrooms.dto;

import com.uti.svcrooms.model.RoomType;
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
    private RoomType type;
    private Double pricePerNight;
    private Integer totalCapacity;
    private Integer availableRooms;
    private Integer floor;
    private String description;
    private Boolean available;
}
