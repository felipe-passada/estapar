package com.passada.felipe.estapar.domain.service;

import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupancyService {

    private final SpotRepository spotRepository;

    /**
     *
     * @param sectorName
     * @return occupancy percentual (0.0 to 1.0)
     */
    public double getOccupancyRate(String sectorName) {
        List<Spot> spots = spotRepository.findBySectorName(sectorName);

        if (spots.isEmpty()) {
            return 0.0;
        }

        long occupiedCount = spots.stream()
                .filter(Spot::isOccupied)
                .count();

        return (double) occupiedCount / spots.size();
    }

    /**
     *
     * @param sectorName
     * @return true if sector is full
     */
    public boolean isSectorFull(String sectorName) {
        return getOccupancyRate(sectorName) >= 1.0;
    }


    /**
     *
     * @return true if all sectors are full (garage is full)
     */
    public boolean isGarageFull() {
        List<Spot> allSpots = spotRepository.findAll();

        if (allSpots.isEmpty()) {
            return true;
        }

        return allSpots.stream().noneMatch(spot -> !spot.isOccupied());
    }
}
