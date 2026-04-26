package com.saquib.apts.Entity;

import com.saquib.apts.Enums.SpotType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "parking_spot")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotType spotType;

    @Column(nullable = false)
    private boolean isAvailable;

    @Version
    private Long version;

}
