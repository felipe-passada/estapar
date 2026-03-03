package com.passada.felipe.estapar.domain.service;

import com.passada.felipe.estapar.domain.service.pricing.PricingModifierFactory;
import com.passada.felipe.estapar.domain.service.pricing.PricingModifierStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PricingService {

    private static final long FREE_MINUTES = 30;

    private final OccupancyService occupancyService;
    private final PricingModifierFactory pricingModifierFactory;

    /**
     * Based on sector occupancy return a price modifier to be applied on top of the base price.
     *
     * @param basePrice  sector base price
     * @param sectorName name of the sector where the price is being calculated
     * @return hourly price to be applied at entry
     */
    public BigDecimal calculateAppliedPrice(BigDecimal basePrice, String sectorName) {
        double occupancyRate = occupancyService.getOccupancyRate(sectorName);
        PricingModifierStrategy strategy = pricingModifierFactory.resolve(occupancyRate);
        BigDecimal modifier = BigDecimal.ONE.add(BigDecimal.valueOf(strategy.getModifier()));
        return basePrice.multiply(modifier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Return the price to be charged at exit based on the time parked and the price applied at entry.
     *
     * @param entryTime moment when the vehicle entered the parking lot
     * @param exitTime moment when the vehicle is leaving the parking lot
     * @param appliedPrice estimated hourly price calculated at entry
     * @return total price to be charged at exit
     */
    public BigDecimal calculateFinalAmount(Instant entryTime, Instant exitTime, BigDecimal appliedPrice) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();

        if (totalMinutes <= FREE_MINUTES) {
            return BigDecimal.ZERO;
        }

        long hours = (long) Math.ceil(totalMinutes / 60.0);
        return appliedPrice.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
    }
}
