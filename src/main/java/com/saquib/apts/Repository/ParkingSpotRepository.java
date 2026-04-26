package com.saquib.apts.Repository;

import com.saquib.apts.Entity.ParkingSpot;
import com.saquib.apts.Enums.SpotType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSpot s WHERE s.spotType = :spotType AND s.isAvailable = true ORDER BY s.id ASC LIMIT 1")
    Optional<ParkingSpot> findFirstAvailableSpotByType(@Param("spotType") SpotType spotType);



}
