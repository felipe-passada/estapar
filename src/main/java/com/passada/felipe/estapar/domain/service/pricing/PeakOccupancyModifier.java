package com.passada.felipe.estapar.domain.service.pricing;

import org.springframework.stereotype.Component;

/**
 * Occupancy 75%-100% → 25% raise.
 */
@Component
public class PeakOccupancyModifier implements PricingModifierStrategy {

    @Override
    public boolean applies(double occupancyRate) {
        return occupancyRate >= 0.75;
    }

    @Override
    public double getModifier() {
        return 0.25;
    }
}
