package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.domain.exception.EntityNotFoundException;
import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.model.RevenueEntry;
import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.domain.repository.RevenueEntryRepository;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.domain.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessExitServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private RevenueEntryRepository revenueEntryRepository;

    @Mock
    private PricingService pricingService;

    @Captor
    private ArgumentCaptor<RevenueEntry> revenueEntryCaptor;

    @Captor
    private ArgumentCaptor<Spot> spotCaptor;

    private ProcessExitService processExitService;

    private static final String LICENSE_PLATE = "ZUL0001";
    private static final Instant ENTRY_TIME = Instant.parse("2025-01-01T12:00:00Z");
    private static final Instant EXIT_TIME = ENTRY_TIME.plus(90, ChronoUnit.MINUTES);
    private static final BigDecimal APPLIED_PRICE = new BigDecimal("12.50");
    private static final BigDecimal TOTAL_FEE = new BigDecimal("25.00");

    @BeforeEach
    void setUp() {
        processExitService = new ProcessExitService(
                parkingSessionRepository,
                spotRepository,
                revenueEntryRepository,
                pricingService
        );
    }

    // ===== Saída normal com vaga vinculada =====

    @Test
    void shouldProcessExitSuccessfullyWithSpotLinked() {
        ParkingSession session = ParkingSession.builder()
                .licensePlate(LICENSE_PLATE)
                .entryTime(ENTRY_TIME)
                .appliedPrice(APPLIED_PRICE)
                .sectorName("A")
                .spotId(1L)
                .build();

        Spot spot = new Spot(1L, "A", -23.561684, -46.655981, true);

        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(session));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(pricingService.calculateFinalAmount(ENTRY_TIME, EXIT_TIME, APPLIED_PRICE)).thenReturn(TOTAL_FEE);

        processExitService.execute(LICENSE_PLATE, EXIT_TIME);

        // Verifica que a vaga foi liberada
        verify(spotRepository).save(spotCaptor.capture());
        assertFalse(spotCaptor.getValue().isOccupied());

        // Verifica que a receita foi salva corretamente
        verify(revenueEntryRepository).save(revenueEntryCaptor.capture());
        RevenueEntry savedEntry = revenueEntryCaptor.getValue();
        assertEquals(LICENSE_PLATE, savedEntry.getLicensePlate());
        assertEquals("A", savedEntry.getSectorName());
        assertEquals(ENTRY_TIME, savedEntry.getEntryTime());
        assertEquals(EXIT_TIME, savedEntry.getExitTime());
        assertEquals(TOTAL_FEE, savedEntry.getTotalAmount());

        // Verifica que a sessão foi removida
        verify(parkingSessionRepository).delete(session);
    }

    @Test
    void shouldCalculateFinalAmountWithCorrectParameters() {
        ParkingSession session = ParkingSession.builder()
                .licensePlate(LICENSE_PLATE)
                .entryTime(ENTRY_TIME)
                .appliedPrice(APPLIED_PRICE)
                .sectorName("A")
                .spotId(1L)
                .build();

        Spot spot = new Spot(1L, "A", -23.561684, -46.655981, true);

        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(session));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(pricingService.calculateFinalAmount(ENTRY_TIME, EXIT_TIME, APPLIED_PRICE)).thenReturn(TOTAL_FEE);

        processExitService.execute(LICENSE_PLATE, EXIT_TIME);

        verify(pricingService).calculateFinalAmount(ENTRY_TIME, EXIT_TIME, APPLIED_PRICE);
    }

    @Test
    void shouldReturnZeroFeeWhenParkedLessThan30Minutes() {
        Instant earlyExit = ENTRY_TIME.plus(15, ChronoUnit.MINUTES);

        ParkingSession session = ParkingSession.builder()
                .licensePlate(LICENSE_PLATE)
                .entryTime(ENTRY_TIME)
                .appliedPrice(APPLIED_PRICE)
                .sectorName("A")
                .spotId(1L)
                .build();

        Spot spot = new Spot(1L, "A", -23.561684, -46.655981, true);

        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(session));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(pricingService.calculateFinalAmount(ENTRY_TIME, earlyExit, APPLIED_PRICE)).thenReturn(BigDecimal.ZERO);

        processExitService.execute(LICENSE_PLATE, earlyExit);

        verify(revenueEntryRepository).save(revenueEntryCaptor.capture());
        assertEquals(BigDecimal.ZERO, revenueEntryCaptor.getValue().getTotalAmount());
        verify(parkingSessionRepository).delete(session);
    }

    // ===== Saída sem PARKED (spotId null) =====

    @Test
    void shouldProcessExitWithoutSpotWhenParkedEventNeverReceived() {
        ParkingSession session = ParkingSession.builder()
                .licensePlate(LICENSE_PLATE)
                .entryTime(ENTRY_TIME)
                .appliedPrice(null)
                .sectorName(null)
                .spotId(null)
                .build();

        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(session));
        when(pricingService.calculateFinalAmount(ENTRY_TIME, EXIT_TIME, null)).thenReturn(BigDecimal.ZERO);

        processExitService.execute(LICENSE_PLATE, EXIT_TIME);

        // Não deve interagir com spotRepository quando spotId é null
        verify(spotRepository, never()).findById(any());
        verify(spotRepository, never()).save(any());

        // Receita ainda é registrada
        verify(revenueEntryRepository).save(revenueEntryCaptor.capture());
        RevenueEntry savedEntry = revenueEntryCaptor.getValue();
        assertEquals(LICENSE_PLATE, savedEntry.getLicensePlate());
        assertNull(savedEntry.getSectorName());
        assertEquals(BigDecimal.ZERO, savedEntry.getTotalAmount());

        // Sessão é removida
        verify(parkingSessionRepository).delete(session);
    }

    // ===== Placa inexistente =====

    @Test
    void shouldThrowEntityNotFoundExceptionWhenLicensePlateNotFound() {
        when(parkingSessionRepository.findByLicensePlate("INVALID")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> processExitService.execute("INVALID", EXIT_TIME)
        );

        assertTrue(exception.getMessage().contains("INVALID"));

        // Nenhuma operação posterior deve ocorrer
        verify(spotRepository, never()).findById(any());
        verify(spotRepository, never()).save(any());
        verify(revenueEntryRepository, never()).save(any());
        verify(parkingSessionRepository, never()).delete(any());
    }

    // ===== Spot não encontrado quando spotId existe =====

    @Test
    void shouldThrowEntityNotFoundExceptionWhenSpotNotFound() {
        ParkingSession session = ParkingSession.builder()
                .licensePlate(LICENSE_PLATE)
                .entryTime(ENTRY_TIME)
                .appliedPrice(APPLIED_PRICE)
                .sectorName("A")
                .spotId(999L)
                .build();

        when(parkingSessionRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(session));
        when(spotRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> processExitService.execute(LICENSE_PLATE, EXIT_TIME)
        );

        assertTrue(exception.getMessage().contains("999"));

        // Não deve calcular preço nem salvar receita
        verify(pricingService, never()).calculateFinalAmount(any(), any(), any());
        verify(revenueEntryRepository, never()).save(any());
        verify(parkingSessionRepository, never()).delete(any());
    }
}

