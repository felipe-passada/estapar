package com.passada.felipe.estapar.domain.service;

import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OccupancyServiceImplTest {

    @Mock
    private SpotRepository spotRepository;

    private OccupancyServiceImpl occupancyService;

    private static final String SECTOR_NAME = "A";

    @BeforeEach
    void setUp() {
        occupancyService = new OccupancyServiceImpl(spotRepository);
    }

    // ===== getOccupancyRate =====

    @Test
    void shouldReturnZeroWhenSectorIsEmpty() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, false),
                createSpot(2L, false),
                createSpot(3L, false),
                createSpot(4L, false)
        ));

        double rate = occupancyService.getOccupancyRate(SECTOR_NAME);

        assertEquals(0.0, rate, 0.001);
    }

    @Test
    void shouldReturnZeroWhenNoSpotsExist() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(Collections.emptyList());

        double rate = occupancyService.getOccupancyRate(SECTOR_NAME);

        assertEquals(0.0, rate, 0.001);
    }

    @Test
    void shouldReturnCorrectRateWhenPartiallyOccupied() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, false),
                createSpot(3L, true),
                createSpot(4L, false)
        ));

        double rate = occupancyService.getOccupancyRate(SECTOR_NAME);

        assertEquals(0.50, rate, 0.001);
    }

    @Test
    void shouldReturn25PercentWhenOneOfFourOccupied() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, false),
                createSpot(3L, false),
                createSpot(4L, false)
        ));

        double rate = occupancyService.getOccupancyRate(SECTOR_NAME);

        assertEquals(0.25, rate, 0.001);
    }

    @Test
    void shouldReturn100PercentWhenSectorIsFull() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, true),
                createSpot(3L, true),
                createSpot(4L, true)
        ));

        double rate = occupancyService.getOccupancyRate(SECTOR_NAME);

        assertEquals(1.0, rate, 0.001);
    }

    // ===== isSectorFull =====

    @Test
    void shouldReturnTrueWhenSectorIsFull() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, true),
                createSpot(3L, true),
                createSpot(4L, true)
        ));

        assertTrue(occupancyService.isSectorFull(SECTOR_NAME));
    }

    @Test
    void shouldReturnFalseWhenSectorHasAvailableSpots() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, true),
                createSpot(3L, true),
                createSpot(4L, false)
        ));

        assertFalse(occupancyService.isSectorFull(SECTOR_NAME));
    }

    @Test
    void shouldReturnFalseWhenSectorIsEmpty() {
        when(spotRepository.findBySectorName(SECTOR_NAME)).thenReturn(List.of(
                createSpot(1L, false),
                createSpot(2L, false),
                createSpot(3L, false),
                createSpot(4L, false)
        ));

        assertFalse(occupancyService.isSectorFull(SECTOR_NAME));
    }

    // ===== isGarageFull =====

    @Test
    void shouldReturnTrueWhenGarageIsFullAllSpotsOccupied() {
        when(spotRepository.findAll()).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, true)
        ));

        assertTrue(occupancyService.isGarageFull());
    }

    @Test
    void shouldReturnTrueWhenGarageHasNoSpots() {
        when(spotRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(occupancyService.isGarageFull());
    }

    @Test
    void shouldReturnFalseWhenGarageHasAvailableSpots() {
        when(spotRepository.findAll()).thenReturn(List.of(
                createSpot(1L, true),
                createSpot(2L, false)
        ));

        assertFalse(occupancyService.isGarageFull());
    }

    // ===== Helper =====

    private Spot createSpot(Long id, boolean occupied) {
        return new Spot(id, SECTOR_NAME, -23.561684, -46.655981, occupied);
    }
}
