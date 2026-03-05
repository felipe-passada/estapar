package com.passada.felipe.estapar.domain.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PricingModifierResolverTest {
    private PricingModifierFactory resolver;

    @BeforeEach
    void setUp() {
        resolver = new PricingModifierFactory(List.of(
                new LowOccupancyModifier(),
                new NormalOccupancyModifier(),
                new HighOccupancyModifier(),
                new PeakOccupancyModifier()
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "0.00, -0.10",
            "0.10, -0.10",
            "0.24, -0.10",
            "0.25,  0.00",
            "0.49,  0.00",
            "0.50,  0.10",
            "0.74,  0.10",
            "0.75,  0.25",
            "1.00,  0.25"
    })
    void shouldResolveCorrectModifierForOccupancyRate(double occupancyRate, double expectedFactor) {
        assertEquals(expectedFactor, resolver.resolve(occupancyRate).getModifier(), 0.001);
    }

    @Test
    void shouldThrowWhenMultipleStrategiesApply() {
        PricingModifierStrategy overlapping = new PricingModifierStrategy() {
            @Override
            public boolean applies(double occupancyRate) {
                return occupancyRate < 0.50;
            }

            @Override
            public double getModifier() {
                return 0.99;
            }
        };

        PricingModifierFactory badResolver = new PricingModifierFactory(List.of(
                new LowOccupancyModifier(),
                overlapping
        ));

        assertThrows(IllegalStateException.class, () -> badResolver.resolve(0.10));
    }
}
