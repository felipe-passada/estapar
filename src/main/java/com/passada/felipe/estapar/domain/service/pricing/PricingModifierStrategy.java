package com.passada.felipe.estapar.domain.service.pricing;

public interface PricingModifierStrategy {

    /**
     * Check if the strategy applies to the occupancy rate.
     *
     * @param occupancyRate between (0.0 to 1.0)
     * @return true if strategy applies
     */
    boolean applies(double occupancyRate);

    /**
     *
     * @return price modifier
     */
    double getModifier();
}
