package com.passada.felipe.estapar.domain.service.pricing;

import org.springframework.stereotype.Component;

/**
 * Occupancy 50%-75% → 10% raise.
 */
@Component
public class HighOccupancyModifier implements PricingModifierStrategy {

    @Override
    public boolean applies(double occupancyRate) {
        return occupancyRate >= 0.50 && occupancyRate < 0.75;
    }

    @Override
    public double getModifier() {
        return 0.10;
    }
}
