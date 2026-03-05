package com.passada.felipe.estapar.domain.service.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NormalOccupancyModifierTest {
    private final NormalOccupancyModifier modifier = new NormalOccupancyModifier();

    @Test
    void shouldApplyWhenOccupancyBetween25And50Percent() {
        assertTrue(modifier.applies(0.25));
        assertTrue(modifier.applies(0.30));
        assertTrue(modifier.applies(0.49));
    }

    @Test
    void shouldNotApplyOutsideRange() {
        assertFalse(modifier.applies(0.24));
        assertFalse(modifier.applies(0.50));
        assertFalse(modifier.applies(0.0));
        assertFalse(modifier.applies(1.0));
    }

    @Test
    void shouldReturnZeroAdjustment() {
        assertEquals(0.0, modifier.getModifier());
    }
}
