package com.passada.felipe.estapar.domain.service.pricing;

import org.springframework.stereotype.Component;

/**
 * Occupancy 25%-50% → pricing doesn't change.
 */
@Component
public class NormalOccupancyModifier implements PricingModifierStrategy {

    @Override
    public boolean applies(double occupancyRate) {
        return occupancyRate >= 0.25 && occupancyRate < 0.50;
    }

    @Override
    public double getModifier() {
        return 0.0;
    }
}
