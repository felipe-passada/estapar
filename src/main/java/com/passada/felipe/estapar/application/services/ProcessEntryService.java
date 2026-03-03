package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.application.usecases.ProcessEntryUseCase;
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

        //TODO: Substitute for a handler that returns a custom exception with error code and message
        parkingSessionRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
                .ifPresent(session -> {
                    throw new IllegalStateException(
                            "Já existe uma sessão ativa para a placa: " + licensePlate);
                });

        if(occupancyService.isGarageFull()) {
            throw new IllegalStateException("Parking lot is full. Cannot process entry for plate: " + licensePlate);
        }

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(licensePlate);
        session.setEntryTime(entryTime);

        parkingSessionRepository.save(session);

        log.info("ENTRY successfully registered: plate={}, entryTime={}", licensePlate, entryTime);
    }
}
