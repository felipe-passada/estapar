package com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository;

import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaSpotRepository extends JpaRepository<SpotEntity, Long> {

    Optional<SpotEntity> findByLatAndLng(Double lat, Double lng);

    List<SpotEntity> findBySectorName(String sectorName);

    long countBySectorNameAndOccupiedTrue(String sectorName);
}
