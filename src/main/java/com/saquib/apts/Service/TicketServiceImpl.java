package com.saquib.apts.Service;

import com.saquib.apts.DTO.TicketResponse;
import com.saquib.apts.DTO.VehicleEntryRequest;
import com.saquib.apts.DTO.VehicleExitRequest;
import com.saquib.apts.Entity.ParkingSpot;
import com.saquib.apts.Entity.Ticket;
import com.saquib.apts.Enums.SpotType;
import com.saquib.apts.Enums.TicketStatus;
import com.saquib.apts.Exception.ResourceNotFoundException;
import com.saquib.apts.Repository.ParkingSpotRepository;
import com.saquib.apts.Repository.TicketRepository;
import com.saquib.apts.Service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ParkingSpotRepository spotRepository;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TicketResponse vehicleEntry(VehicleEntryRequest request) {
        log.info("Vehicle entry initiated for: {}", request.getVehicleNumber());
        validateVehicleNotParked(request.getVehicleNumber());
        ParkingSpot spot = reserveSpot(request.getSpotType(), request.getVehicleNumber());
        Ticket saved = createAndSaveTicket(request.getVehicleNumber(), spot, request.getSpotType());
        log.info("Ticket generated: {}", saved.getTicketNumber());
        return mapToResponse(saved);
    }

    private void validateVehicleNotParked(String vehicleNumber) {
        if (ticketRepository.existsByVehicleNumberAndStatus(vehicleNumber, TicketStatus.ACTIVE)) {
            log.warn("Vehicle already parked: {}", vehicleNumber);
            throw new IllegalArgumentException("Vehicle already parked: " + vehicleNumber);
        }
    }

    private ParkingSpot reserveSpot(SpotType spotType, String vehicleNumber) {
        ParkingSpot spot = spotRepository.findFirstAvailableSpotByType(spotType)
                .orElseThrow(() -> {
                    log.error("No spot available for type: {}", spotType);
                    return new IllegalArgumentException("No spot available for: " + spotType);
                });
        spot.setAvailable(false);
        spotRepository.save(spot);
        log.info("Spot {} reserved for vehicle: {}", spot.getSpotNumber(), vehicleNumber);
        return spot;
    }

    private Ticket createAndSaveTicket(String vehicleNumber, ParkingSpot spot, SpotType spotType) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ticketNumber = "TKT-" + spotType.name().charAt(0) + "-" + date + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Ticket ticket = Ticket.builder()
                .vehicleNumber(vehicleNumber)
                .parkingSpot(spot)
                .entryTime(LocalDateTime.now())
                .ticketNumber(ticketNumber)
                .status(TicketStatus.ACTIVE)
                .build();

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketResponse vehicleExit(VehicleExitRequest request) {
        log.info("Vehicle exit initiated for ticket: {}", request.getTicketNumber());
        Ticket ticket = findActiveTicket(request.getTicketNumber());
        processExit(ticket);
        log.info("Vehicle exited — fee charged: {}", ticket.getTotalFee());
        return mapToResponse(ticketRepository.save(ticket));
    }

    private Ticket findActiveTicket(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> {
                    log.error("Ticket not found: {}", ticketNumber);
                    return new ResourceNotFoundException("Ticket not found: " + ticketNumber);
                });
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            log.warn("Vehicle already exited for ticket: {}", ticketNumber);
            throw new IllegalArgumentException("Already exited: " + ticketNumber);
        }
        return ticket;
    }

    private void processExit(Ticket ticket) {
        LocalDateTime exitTime = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), exitTime);
        if (hours == 0) hours = 1;
        double fee = hours * ticket.getParkingSpot().getSpotType().getPrice();
        ticket.setExitTime(exitTime);
        ticket.setTotalFee(fee);
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.getParkingSpot().setAvailable(true);
        spotRepository.save(ticket.getParkingSpot());
    }

    @Override
    public TicketResponse getTicketByVehicle(String vehicleNumber) {
        log.info("Getting ticketNumber for vehicle: {}", vehicleNumber);
        Ticket ticket = findTicketByVehicleNumber(vehicleNumber);
        return mapToResponse(ticket);
    }

    private Ticket findTicketByVehicleNumber(String vehicleNumber) {
        Ticket ticket = ticketRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> {
                    log.error("Ticket not found for vehicle: {}", vehicleNumber);
                    return new ResourceNotFoundException("Ticket not found for vehicle: " + vehicleNumber);
                });
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            log.warn("Vehicle already exited");
            throw new IllegalArgumentException("Already exited");
        }
        return ticket;
    }


    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setTicketNumber(ticket.getTicketNumber());
        response.setVehicleNumber(ticket.getVehicleNumber());
        response.setSpotNumber(ticket.getParkingSpot().getSpotNumber());
        response.setEntryTime(ticket.getEntryTime());
        response.setExitTime(ticket.getExitTime());
        response.setTotalFee(ticket.getTotalFee());
        response.setStatus(ticket.getStatus());
        return response;
    }
}