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
public class PricingServiceImpl implements PricingService {

    private static final long FREE_MINUTES = 30;

    private final OccupancyService occupancyService;
    private final PricingModifierFactory pricingModifierFactory;

    @Override
    public BigDecimal calculateAppliedPrice(BigDecimal basePrice, String sectorName) {
        double occupancyRate = occupancyService.getOccupancyRate(sectorName);
        PricingModifierStrategy strategy = pricingModifierFactory.resolve(occupancyRate);
        BigDecimal modifier = BigDecimal.ONE.add(BigDecimal.valueOf(strategy.getModifier()));
        return basePrice.multiply(modifier).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateFinalAmount(Instant entryTime, Instant exitTime, BigDecimal appliedPrice) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();

        if (totalMinutes <= FREE_MINUTES) {
            return BigDecimal.ZERO;
        }

        long hours = (long) Math.ceil(totalMinutes / 60.0);
        return appliedPrice.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
    }
}
