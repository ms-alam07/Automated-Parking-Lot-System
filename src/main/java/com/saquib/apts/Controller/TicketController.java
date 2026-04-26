package com.saquib.apts.Controller;

import com.saquib.apts.DTO.TicketResponse;
import com.saquib.apts.DTO.VehicleEntryRequest;
import com.saquib.apts.DTO.VehicleExitRequest;
import com.saquib.apts.Service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/entry")
    public ResponseEntity<TicketResponse> entry(@RequestBody @Validated VehicleEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.vehicleEntry(request));
    }

    @PostMapping("/exit")
    public ResponseEntity<TicketResponse> exit(@RequestBody VehicleExitRequest request) {
        return ResponseEntity.ok(ticketService.vehicleExit(request));
    }

    @GetMapping("/vehicle/{vehicleNumber}")
    public ResponseEntity<TicketResponse> getTicketByVehicle(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(ticketService.getTicketByVehicle(vehicleNumber));
    }
}