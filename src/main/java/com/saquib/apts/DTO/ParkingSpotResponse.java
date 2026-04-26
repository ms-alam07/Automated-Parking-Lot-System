package com.saquib.apts.DTO;

import com.saquib.apts.Enums.SpotType;
import lombok.*;

@Data
public class ParkingSpotResponse {
    private Long id;
    private String spotNumber;
    private SpotType spotType;
    private boolean isAvailable;
}
