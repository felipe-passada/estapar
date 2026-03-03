package com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository;

import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.ParkingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    Optional<ParkingSessionEntity> findByLicensePlate(String licensePlate);
    Optional<ParkingSessionEntity> findBySpotId(Long spotId);
    Optional<ParkingSessionEntity> findByLicensePlateAndExitTimeIsNull(String licensePlate);

}
