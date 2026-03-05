package com.passada.felipe.estapar.domain.service;

import com.passada.felipe.estapar.domain.service.pricing.PricingModifierFactory;
import com.passada.felipe.estapar.domain.service.pricing.PricingModifierStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private OccupancyService occupancyService;

    @Mock
    private PricingModifierFactory pricingModifierFactory;

    @Mock
    private PricingModifierStrategy pricingModifierStrategy;

    private PricingServiceImpl pricingService;

    private static final BigDecimal BASE_PRICE = new BigDecimal("10.00");

    @BeforeEach
    void setUp() {
        pricingService = new PricingServiceImpl(occupancyService, pricingModifierFactory);
    }

    // ===== calculateAppliedPrice =====

    @Test
    void shouldApply10PercentDiscountWhenOccupancyBelow25() {
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.10);
        when(pricingModifierFactory.resolve(0.10)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(-0.10);

        BigDecimal result = pricingService.calculateAppliedPrice(BASE_PRICE, "A");

        // 10.00 × (1 + (-0.10)) = 10.00 × 0.90 = 9.00
        assertEquals(new BigDecimal("9.00"), result);
    }

    @Test
    void shouldApplyNoModifierWhenOccupancyBetween25And50() {
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.30);
        when(pricingModifierFactory.resolve(0.30)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(0.0);

        BigDecimal result = pricingService.calculateAppliedPrice(BASE_PRICE, "A");

        // 10.00 × (1 + 0.00) = 10.00
        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldApply10PercentIncreaseWhenOccupancyBetween50And75() {
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.60);
        when(pricingModifierFactory.resolve(0.60)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(0.10);

        BigDecimal result = pricingService.calculateAppliedPrice(BASE_PRICE, "A");

        // 10.00 × (1 + 0.10) = 11.00
        assertEquals(new BigDecimal("11.00"), result);
    }

    @Test
    void shouldApply25PercentIncreaseWhenOccupancyAbove75() {
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.80);
        when(pricingModifierFactory.resolve(0.80)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(0.25);

        BigDecimal result = pricingService.calculateAppliedPrice(BASE_PRICE, "A");

        // 10.00 × (1 + 0.25) = 12.50
        assertEquals(new BigDecimal("12.50"), result);
    }

    // ===== calculateFinalAmount — primeiros 30 minutos grátis =====

    @Test
    void shouldReturnZeroWhenParkedExactly30Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(30, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldReturnZeroWhenParkedLessThan30Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(15, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldReturnZeroWhenParked1Minute() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(1, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        assertEquals(BigDecimal.ZERO, result);
    }

    // ===== calculateFinalAmount — cobrança por hora (arredondamento para cima) =====

    @Test
    void shouldCharge1HourWhenParked31Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(31, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        // ceil(31/60) = 1 hora × 10.00 = 10.00
        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldCharge1HourWhenParkedExactly60Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(60, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        // ceil(60/60) = 1 hora × 10.00 = 10.00
        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void shouldCharge2HoursWhenParked61Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(61, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        // ceil(61/60) = 2 horas × 10.00 = 20.00
        assertEquals(new BigDecimal("20.00"), result);
    }

    @Test
    void shouldCharge3HoursWhenParked150Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(150, ChronoUnit.MINUTES);

        BigDecimal result = pricingService.calculateFinalAmount(entry, exit, BASE_PRICE);

        // ceil(150/60) = 3 horas × 10.00 = 30.00
        assertEquals(new BigDecimal("30.00"), result);
    }

    // ===== Combinação: appliedPrice com modifier + finalAmount com tempo =====

    @Test
    void shouldCombineHourRoundingWithDynamicPricing() {
        // Etapa 1: calcula o preço ajustado na entrada
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.80);
        when(pricingModifierFactory.resolve(0.80)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(0.25);

        BigDecimal appliedPrice = pricingService.calculateAppliedPrice(BASE_PRICE, "A");
        assertEquals(new BigDecimal("12.50"), appliedPrice);

        // Etapa 2: calcula o valor final na saída
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(91, ChronoUnit.MINUTES);

        BigDecimal finalAmount = pricingService.calculateFinalAmount(entry, exit, appliedPrice);

        // ceil(91/60) = 2 horas × 12.50 = 25.00
        assertEquals(new BigDecimal("25.00"), finalAmount);
    }

    @Test
    void shouldCombineDiscountWithMultipleHours() {
        // Etapa 1: preço com desconto de 10%
        when(occupancyService.getOccupancyRate("A")).thenReturn(0.10);
        when(pricingModifierFactory.resolve(0.10)).thenReturn(pricingModifierStrategy);
        when(pricingModifierStrategy.getModifier()).thenReturn(-0.10);

        BigDecimal appliedPrice = pricingService.calculateAppliedPrice(BASE_PRICE, "A");
        assertEquals(new BigDecimal("9.00"), appliedPrice);

        // Etapa 2: 2 horas com preço com desconto
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(120, ChronoUnit.MINUTES);

        BigDecimal finalAmount = pricingService.calculateFinalAmount(entry, exit, appliedPrice);

        // ceil(120/60) = 2 horas × 9.00 = 18.00
        assertEquals(new BigDecimal("18.00"), finalAmount);
    }

    @Test
    void shouldReturnZeroRegardlessOfAppliedPriceWhenUnder30Minutes() {
        Instant entry = Instant.parse("2025-01-01T12:00:00Z");
        Instant exit = entry.plus(25, ChronoUnit.MINUTES);

        BigDecimal appliedPrice = new BigDecimal("12.50");

        BigDecimal finalAmount = pricingService.calculateFinalAmount(entry, exit, appliedPrice);

        assertEquals(BigDecimal.ZERO, finalAmount);
    }
}
