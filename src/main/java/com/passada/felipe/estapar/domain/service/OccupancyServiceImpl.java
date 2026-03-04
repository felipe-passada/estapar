package com.passada.felipe.estapar.domain.service;

import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupancyServiceImpl implements OccupancyService {

    private final SpotRepository spotRepository;

    @Override
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

    @Override
    public boolean isSectorFull(String sectorName) {
        return getOccupancyRate(sectorName) >= 1.0;
    }

    @Override
    public boolean isGarageFull() {
        List<Spot> allSpots = spotRepository.findAll();

        if (allSpots.isEmpty()) {
            return true;
        }

        return allSpots.stream().noneMatch(spot -> !spot.isOccupied());
    }
}
