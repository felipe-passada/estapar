package com.passada.felipe.estapar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSession {
    private String licensePlate;
    private Instant entryTime;
    private BigDecimal appliedPrice;
    private String sectorName;
    private Long spotId;
}
