package com.passada.felipe.estapar.domain.repository;

import com.passada.felipe.estapar.domain.model.ParkingSession;

import java.util.Optional;

public interface ParkingSessionRepository {

    Optional<ParkingSession> findByLicensePlate(String licensePlate);

    Optional<ParkingSession> findBySpotId(Long spotId);

    ParkingSession save(ParkingSession parkingSession);

    void delete(ParkingSession parkingSession);

    void deleteAll();
}
