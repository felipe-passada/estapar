package com.passada.felipe.estapar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spot {
    private String id;
    private String sectorName;
    private Double latitude;
    private Double longitude;
    private boolean occupied;
}
