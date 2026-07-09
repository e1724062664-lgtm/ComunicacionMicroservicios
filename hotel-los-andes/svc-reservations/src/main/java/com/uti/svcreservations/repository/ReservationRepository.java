package com.uti.svcreservations.repository;

import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Obtiene reserva asociada a un correo de huesped
    List<Reservation> findByGuestEmail(String guestEmail);

    // No permitir dos reservas ACTIVE del mismo huesped
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.roomId = :roomId " +
            "AND r.guestEmail = :guestEmail AND r.status = 'ACTIVE'")
    boolean existsActiveReservationForGuestAndRoom(@Param("roomId") Long roomId,
                                                    @Param("guestEmail") String guestEmail);
}
