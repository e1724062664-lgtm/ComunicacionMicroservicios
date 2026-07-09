package com.uti.svcrooms.mapper;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;
import com.uti.svcrooms.model.Room;
import org.springframework.stereotype.Component;


@Component
public class RoomMapper {


    public Room toEntity(RoomRequest request) {
        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .totalCapacity(request.getTotalCapacity())
                .availableRooms(request.getAvailableRooms())
                .floor(request.getFloor())
                .description(request.getDescription())
                .build();
    }


    public RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .totalCapacity(room.getTotalCapacity())
                .availableRooms(room.getAvailableRooms())
                .floor(room.getFloor())
                .description(room.getDescription())
                .available(room.getAvailableRooms() != null && room.getAvailableRooms() > 0)
                .build();
    }


    public AvailabilityResponse toAvailabilityResponse(Room room) {
        return AvailabilityResponse.builder()
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .available(room.getAvailableRooms() != null && room.getAvailableRooms() > 0)
                .availableRooms(room.getAvailableRooms())
                .build();
    }
}
