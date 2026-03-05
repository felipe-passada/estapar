package com.passada.felipe.estapar.domain.service.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LowOccupancyModifierTest {

    private final LowOccupancyModifier modifier = new LowOccupancyModifier();

    @Test
    void shouldApplyWhenOccupancyBelow25Percent() {
        assertTrue(modifier.applies(0.0));
        assertTrue(modifier.applies(0.10));
        assertTrue(modifier.applies(0.24));
    }

    @Test
    void shouldNotApplyWhenOccupancyAt25PercentOrAbove() {
        assertFalse(modifier.applies(0.25));
        assertFalse(modifier.applies(0.50));
        assertFalse(modifier.applies(1.0));
    }

    @Test
    void shouldReturnNegative10PercentDiscount() {
        assertEquals(-0.10, modifier.getModifier());
    }
}
