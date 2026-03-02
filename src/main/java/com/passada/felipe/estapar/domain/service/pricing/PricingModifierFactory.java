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
        return strategies.stream()
                .filter(strategy -> strategy.applies(occupancyRate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhum modificador de preço encontrado para ocupação: " + occupancyRate));
    }
}
