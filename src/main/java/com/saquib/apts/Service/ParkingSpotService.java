package com.saquib.apts.Service;

import com.saquib.apts.DTO.*;
import com.saquib.apts.DTO.ParkingSpotResponse;

import java.util.List;

public interface ParkingSpotService {

    List<ParkingSpotResponse> createBulkSpots(BulkSpotRequest request);

    ParkingSpotResponse createSpot(ParkingSpotRequest request);

    List<ParkingSpotResponse> getAllSpots();

    ParkingSpotResponse getSpotById(Long id);

    List<ParkingSpotResponse> getSpotsByType(String spotType);

    List<ParkingSpotResponse> getAvailableSpots();

    ParkingSpotResponse updateSpot(Long id, ParkingSpotRequest request);

    void deleteSpot(Long id);


}
