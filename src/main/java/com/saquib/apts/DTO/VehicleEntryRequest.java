package com.saquib.apts.DTO;

import com.saquib.apts.Enums.SpotType;
import lombok.Data;

@Data
public class VehicleEntryRequest {
    private String vehicleNumber;
    private SpotType spotType;
}
