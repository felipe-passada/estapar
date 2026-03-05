package com.passada.felipe.estapar.domain.service.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HighOccupancyModifierTest {
    private final HighOccupancyModifier modifier = new HighOccupancyModifier();

    @Test
    void shouldApplyWhenOccupancyBetween50And75Percent() {
        assertTrue(modifier.applies(0.50));
        assertTrue(modifier.applies(0.60));
        assertTrue(modifier.applies(0.74));
    }

    @Test
    void shouldNotApplyOutsideRange() {
        assertFalse(modifier.applies(0.49));
        assertFalse(modifier.applies(0.75));
        assertFalse(modifier.applies(0.0));
        assertFalse(modifier.applies(1.0));
    }

    @Test
    void shouldReturnPositive10PercentIncrease() {
        assertEquals(0.10, modifier.getModifier());
    }
}
