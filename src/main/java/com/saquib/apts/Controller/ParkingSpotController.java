package com.saquib.apts.Controller;


import com.saquib.apts.DTO.BulkSpotRequest;
import com.saquib.apts.DTO.ParkingSpotRequest;
import com.saquib.apts.DTO.ParkingSpotResponse;
import com.saquib.apts.Service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;


    @PostMapping("/bulk")
    public ResponseEntity<List<ParkingSpotResponse>> createBulkSpots(@RequestBody BulkSpotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingSpotService.createBulkSpots(request));
    }

    @PostMapping
    public ResponseEntity<ParkingSpotResponse> createSpot(@RequestBody ParkingSpotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingSpotService.createSpot(request));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotResponse>> getAllSpots() {
        return ResponseEntity.ok(parkingSpotService.getAllSpots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotResponse> getSpotById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpotService.getSpotById(id));
    }

    @GetMapping("/type/{slotType}")
    public ResponseEntity<List<ParkingSpotResponse>> getSpotsByType(@PathVariable String slotType) {
        return ResponseEntity.ok(parkingSpotService.getSpotsByType(slotType));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpotResponse>> getAvailableSpots() {
        return ResponseEntity.ok(parkingSpotService.getAvailableSpots());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpotResponse> updateSpot(@PathVariable Long id,
                                                          @RequestBody ParkingSpotRequest request) {
        return ResponseEntity.ok(parkingSpotService.updateSpot(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        parkingSpotService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}