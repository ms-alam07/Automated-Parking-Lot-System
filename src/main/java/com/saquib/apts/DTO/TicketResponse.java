package com.saquib.apts.DTO;

import com.saquib.apts.Enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private String ticketNumber;
    private String vehicleNumber;
    private String spotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double totalFee;
    private TicketStatus status;
}
