package com.passada.felipe.estapar.domain.service.pricing;

import org.springframework.stereotype.Component;

/**
 * Occupancy < 25% → 10% discount.
 */
@Component
public class LowOccupancyModifier implements PricingModifierStrategy {

    @Override
    public boolean applies(double occupancyRate) {
        return occupancyRate < 0.25;
    }

    @Override
    public double getModifier() {
        return -0.10;
    }
}
