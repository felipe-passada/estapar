package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.application.usecases.ProcessExitUseCase;
import com.passada.felipe.estapar.domain.exception.EntityNotFoundException;
import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.model.RevenueEntry;
import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.domain.repository.RevenueEntryRepository;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.domain.service.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessExitService implements ProcessExitUseCase {

    private final ParkingSessionRepository parkingSessionRepository;
    private final SpotRepository spotRepository;
    private final RevenueEntryRepository revenueEntryRepository;
    private final PricingService pricingService;

    @Override
    @Transactional
    public void execute(String licensePlate, Instant exitTime) {
        log.info("Processing EXIT: plate={}, exitTime={}", licensePlate, exitTime);
        ParkingSession session = parkingSessionRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active parking session not found for license plate: " + licensePlate));

        if (session.getSpotId() != null) {
            Spot spot = spotRepository.findById(session.getSpotId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parking spot not found for session: " + session.getSpotId()));

            spot.setOccupied(false);
            spotRepository.save(spot);
            log.info("Spot {} marked as unoccupied", spot.getId());
        }

        BigDecimal totalFee = pricingService.calculateFinalAmount(
                session.getEntryTime(),
                exitTime,
                session.getAppliedPrice()
        );

        RevenueEntry revenueEntry = RevenueEntry.builder()
                .licensePlate(licensePlate)
                .sectorName(session.getSectorName())
                .entryTime(session.getEntryTime())
                .exitTime(exitTime)
                .totalAmount(totalFee)
                .date(LocalDate.ofInstant(exitTime, java.time.ZoneId.systemDefault()))
                .build();

        revenueEntryRepository.save(revenueEntry);

        parkingSessionRepository.delete(session);
        log.info("EXIT processed successfully: plate={}, totalFee={}", licensePlate, totalFee);
    }
}
