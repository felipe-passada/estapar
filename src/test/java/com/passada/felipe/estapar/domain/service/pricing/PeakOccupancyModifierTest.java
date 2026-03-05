package com.passada.felipe.estapar.domain.service.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeakOccupancyModifierTest {
    private final PeakOccupancyModifier modifier = new PeakOccupancyModifier();

    @Test
    void shouldApplyWhenOccupancyBetween75And100Percent() {
        assertTrue(modifier.applies(0.75));
        assertTrue(modifier.applies(0.90));
        assertTrue(modifier.applies(1.0));
    }

    @Test
    void shouldNotApplyBelow75Percent() {
        assertFalse(modifier.applies(0.74));
        assertFalse(modifier.applies(0.50));
        assertFalse(modifier.applies(0.0));
    }

    @Test
    void shouldReturnPositive25PercentIncrease() {
        assertEquals(0.25, modifier.getModifier());
    }
}
