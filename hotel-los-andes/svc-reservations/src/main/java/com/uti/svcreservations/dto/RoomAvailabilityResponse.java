package com.uti.svcreservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityResponse {

    private Long roomId;
    private String roomNumber;
    private boolean available;
    private Integer availableRooms;
}
