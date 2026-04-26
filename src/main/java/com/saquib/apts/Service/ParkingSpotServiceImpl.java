package com.saquib.apts.Service;

import com.saquib.apts.DTO.BulkSpotRequest;
import com.saquib.apts.DTO.ParkingSpotRequest;
import com.saquib.apts.DTO.ParkingSpotResponse;
import com.saquib.apts.Entity.ParkingSpot;

import com.saquib.apts.Enums.SpotType;
import com.saquib.apts.Exception.ResourceNotFoundException;
import com.saquib.apts.Repository.ParkingSpotRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final ParkingSpotRepository spotRepository;

    @Override
    @Transactional
    public List<ParkingSpotResponse> createBulkSpots(BulkSpotRequest request) {
        log.info("Creating {} spots of type: {}", request.getCount(), request.getSpotType());
        List<ParkingSpotResponse> spots = new ArrayList<>();
        try {
            for (int i = 1; i <= request.getCount(); i++) {
                ParkingSpotRequest spotRequest = new ParkingSpotRequest();
                spotRequest.setSpotNumber(request.getPrefix() + i);
                spotRequest.setSpotType(request.getSpotType());
                spots.add(createSpot(spotRequest));
                log.info("Spot {}{} created successfully", request.getPrefix(), i);
            }
        } catch (Exception e) {
            log.error("Bulk creation failed at spot — rolling back all {} spots: {}", spots.size(), e.getMessage());
            throw e;
        }
        log.info("Bulk spot creation done — total created: {}", spots.size());
        return spots;
    }

    @Override
    @Transactional
    public ParkingSpotResponse createSpot(ParkingSpotRequest request) {
        log.info("Creating spot: {}", request.getSpotNumber());
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotNumber(request.getSpotNumber());
        spot.setSpotType(request.getSpotType());
        spot.setAvailable(true);
        return mapToResponse(spotRepository.save(spot));
    }

    @Override
    public List<ParkingSpotResponse> getAllSpots() {
        List<ParkingSpot> spots = spotRepository.findAll();
        List<ParkingSpotResponse> response = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            response.add(mapToResponse(spot));
        }
        return response;
    }

    @Override
    public ParkingSpotResponse getSpotById(Long id) {
        return mapToResponse(findSpotOrThrow(id));
    }

    @Override
    public List<ParkingSpotResponse> getSpotsByType(String spotType) {
        return spotRepository.findAll()
                .stream()
                .filter(s -> s.getSpotType() == SpotType.valueOf(spotType.toUpperCase()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingSpotResponse> getAvailableSpots() {
        return spotRepository.findAll()
                .stream()
                .filter(ParkingSpot::isAvailable)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ParkingSpotResponse updateSpot(Long id, ParkingSpotRequest request) {
        log.info("Updating spot id: {}", id);
        ParkingSpot spot = findSpotOrThrow(id);
        spot.setSpotNumber(request.getSpotNumber());
        spot.setSpotType(request.getSpotType());
        return mapToResponse(spotRepository.save(spot));
    }

    @Override
    @Transactional
    public void deleteSpot(Long id) {
        log.info("Deleting spot id: {}", id);
        spotRepository.delete(findSpotOrThrow(id));
    }

    private ParkingSpot findSpotOrThrow(Long id) {
        return spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id: " + id));
    }

    private ParkingSpotResponse mapToResponse(ParkingSpot spot) {
        ParkingSpotResponse response = new ParkingSpotResponse();
        response.setId(spot.getId());
        response.setSpotNumber(spot.getSpotNumber());
        response.setSpotType(spot.getSpotType());
        response.setAvailable(spot.isAvailable());
        return response;
    }
}