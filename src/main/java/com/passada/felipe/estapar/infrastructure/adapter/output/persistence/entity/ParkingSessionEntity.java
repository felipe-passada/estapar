package com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "parking_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 20, unique = true)
    private String licensePlate;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "applied_price", precision = 10, scale = 2)
    private BigDecimal appliedPrice;

    @Column(name = "sector_name", length = 50)
    private String sectorName;

    @Column(name = "spot_id")
    private Long spotId;
}
