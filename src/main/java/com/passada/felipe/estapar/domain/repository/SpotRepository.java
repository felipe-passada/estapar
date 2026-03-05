package com.passada.felipe.estapar.domain.repository;

import com.passada.felipe.estapar.domain.model.Spot;

import java.util.List;
import java.util.Optional;

public interface SpotRepository {

    Optional<Spot> findById(Long id);

    Optional<Spot> findByLatitudeAndLongitude(Double latitude, Double longitude);

    List<Spot> findBySectorName(String sectorName);

    Spot save(Spot spot);

    List<Spot> findAll();

    void saveAll(List<Spot> newSpots);

    void deleteAll();
}
