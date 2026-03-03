package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.application.usecases.ProcessParkedUseCase;
import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.model.Sector;
import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.domain.repository.SectorRepository;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.domain.service.OccupancyService;
import com.passada.felipe.estapar.domain.service.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessParkedService implements ProcessParkedUseCase {
    private final SpotRepository spotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final SectorRepository sectorRepository;
    private final OccupancyService occupancyService;
    private final PricingService pricingService;

    @Override
    @Transactional
    public void execute(String licensePlate, Double latitude, Double longitude) {
        log.info("PARKED processing: plate={}, lat={}, lng={}", licensePlate, latitude, longitude);

        ParkingSession session = parkingSessionRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Active parking session not found for license plate: " + licensePlate));

        Spot spot = spotRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseThrow(() -> new IllegalArgumentException("Parking spot not found at the given location."));

        if (spot.isOccupied()) throw new IllegalStateException("Parking spot at the given location is already occupied.");

        String sectorName = spot.getSectorName();

        if (occupancyService.isSectorFull(sectorName)) {
            throw new IllegalStateException(
                    "Sector " + sectorName + " is full. Unable to find a parking spot.");
        }

        Sector sector = sectorRepository.findByName(sectorName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sector not found: " + sectorName));

        BigDecimal appliedPrice = pricingService.calculateAppliedPrice(
                sector.getBasePrice(), sectorName);

        session.setSectorName(sectorName);
        session.setSpotId(spot.getId());
        session.setAppliedPrice(appliedPrice);

        parkingSessionRepository.save(session);

        spot.setOccupied(true);
        spotRepository.save(spot);

        log.info("PARKED successfully registered: plate={}, sector={}, spot={}, appliedPrice={}",
                licensePlate, sectorName, spot.getId(), appliedPrice);
    }
}
