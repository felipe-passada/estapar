package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.application.usecases.ProcessEntryUseCase;
import com.passada.felipe.estapar.domain.exception.DuplicatedSessionException;
import com.passada.felipe.estapar.domain.exception.GarageFullException;
import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.domain.service.OccupancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessEntryService implements ProcessEntryUseCase {
    private final ParkingSessionRepository parkingSessionRepository;
    private final OccupancyService occupancyService;

    @Override
    public void execute(String licensePlate, Instant entryTime) {
        log.info("Processing ENTRY: plate={}, entryTime={}", licensePlate, entryTime);

        parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
                .ifPresent(session -> {
                    throw new DuplicatedSessionException(licensePlate);
                });

        if(occupancyService.isGarageFull()) {
            throw new GarageFullException(licensePlate);
        }

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(licensePlate);
        session.setEntryTime(entryTime);

        parkingSessionRepository.save(session);

        log.info("ENTRY successfully registered: plate={}, entryTime={}", licensePlate, entryTime);
    }
}
