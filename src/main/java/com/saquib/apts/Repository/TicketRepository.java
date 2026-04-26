package com.saquib.apts.Repository;

import com.saquib.apts.Entity.Ticket;
import com.saquib.apts.Enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    boolean existsByVehicleNumberAndStatus(String vehicleNumber, TicketStatus status);

    Optional<Ticket> findByVehicleNumber(String vehicleNumber);

}
