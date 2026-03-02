package com.passada.felipe.estapar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueEntry {
    private Long id;
    private String licensePlate;
    private Double totalAmount;
    private String sectorName;
    private Instant entryTime;
    private Instant exitTime;
    private LocalDate date;
}
