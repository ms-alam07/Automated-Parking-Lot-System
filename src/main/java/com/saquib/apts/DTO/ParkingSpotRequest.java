package com.saquib.apts.DTO;

import com.saquib.apts.Enums.SpotType;
import lombok.Data;

@Data
public class ParkingSpotRequest {
    private String spotNumber;
    private SpotType spotType;
}
