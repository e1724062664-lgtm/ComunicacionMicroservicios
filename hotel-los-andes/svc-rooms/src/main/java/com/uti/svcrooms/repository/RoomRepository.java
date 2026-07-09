package com.uti.svcrooms.repository;

import com.uti.svcrooms.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Verifica si ya existe una habitacion
    boolean existsByRoomNumber(String roomNumber);
}
