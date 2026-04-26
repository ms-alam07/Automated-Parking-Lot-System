package com.saquib.apts.Service;

import com.saquib.apts.DTO.TicketResponse;
import com.saquib.apts.DTO.VehicleEntryRequest;
import com.saquib.apts.DTO.VehicleExitRequest;

import java.util.List;

public interface TicketService {

    TicketResponse vehicleEntry(VehicleEntryRequest request);

    TicketResponse vehicleExit(VehicleExitRequest request);

    TicketResponse getTicketByVehicle(String vehicleNumber);
}