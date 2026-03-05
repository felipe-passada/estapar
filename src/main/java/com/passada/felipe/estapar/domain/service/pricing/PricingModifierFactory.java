package com.passada.felipe.estapar.domain.service.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingModifierFactory {

    private final List<PricingModifierStrategy> strategies;

    /**
     * Returns a matching strategy for the occupancy rate.
     *
     * @param occupancyRate between (0.0 to 1.0)
     * @return matching strategy
     * @throws IllegalStateException if strategy not found for occupancy rate
     */
    public PricingModifierStrategy resolve(double occupancyRate) {
        List<PricingModifierStrategy> matching = strategies.stream()
                .filter(strategy -> strategy.applies(occupancyRate))
                .toList();

        if (matching.size() > 1) {
            throw new IllegalStateException(
                    "Multiple strategies available for occupancy rate: " + occupancyRate);
        }

        return matching.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Price modifier not found for: " + occupancyRate));
    }
}
