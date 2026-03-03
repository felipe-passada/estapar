package com.passada.felipe.estapar.infrastructure.adapter.output.persistence;

import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.ParkingSessionEntity;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository.JpaParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ParkingSessionRepositoryAdapter implements ParkingSessionRepository {

    private final JpaParkingSessionRepository jpaParkingSessionRepository;

    @Override
    public Optional<ParkingSession> findByLicensePlate(String licensePlate) {
        return jpaParkingSessionRepository.findByLicensePlate(licensePlate)
                .map(this::toDomain);
    }

    @Override
    public Optional<ParkingSession> findBySpotId(Long spotId) {
        return jpaParkingSessionRepository.findBySpotId(spotId)
                .map(this::toDomain);
    }

    @Override
    public ParkingSession save(ParkingSession session) {
        ParkingSessionEntity entity = toEntity(session);
        ParkingSessionEntity saved = jpaParkingSessionRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void delete(ParkingSession session) {
        jpaParkingSessionRepository.findByLicensePlate(session.getLicensePlate())
                .ifPresent(jpaParkingSessionRepository::delete);
    }

    @Override
    public Optional<ParkingSession> findByLicensePlateAndExitTimeIsNull(String licensePlate) {
        return jpaParkingSessionRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
                .map(this::toDomain);
    }


    private ParkingSession toDomain(ParkingSessionEntity entity) {
        return ParkingSession.builder()
                .licensePlate(entity.getLicensePlate())
                .entryTime(entity.getEntryTime())
                .appliedPrice(entity.getAppliedPrice())
                .sectorName(entity.getSectorName())
                .spotId(entity.getSpotId())
                .build();
    }

    private ParkingSessionEntity toEntity(ParkingSession session) {
        ParkingSessionEntity entity = jpaParkingSessionRepository
                .findByLicensePlate(session.getLicensePlate())
                .orElse(new ParkingSessionEntity());

        entity.setLicensePlate(session.getLicensePlate());
        entity.setEntryTime(session.getEntryTime());
        entity.setAppliedPrice(session.getAppliedPrice());
        entity.setSectorName(session.getSectorName());
        entity.setSpotId(session.getSpotId());
        return entity;
    }
}
