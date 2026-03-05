package com.passada.felipe.estapar.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.passada.felipe.estapar.domain.model.ParkingSession;
import com.passada.felipe.estapar.domain.model.RevenueEntry;
import com.passada.felipe.estapar.domain.model.Sector;
import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.ParkingSessionRepository;
import com.passada.felipe.estapar.domain.repository.RevenueEntryRepository;
import com.passada.felipe.estapar.domain.repository.SectorRepository;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.RevenueRequest;
import com.passada.felipe.estapar.integration.util.WebhookEventTestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ParkingRevenueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private RevenueEntryRepository revenueEntryRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @BeforeEach
    void setup() {
        // Limpa o banco antes de cada teste
        revenueEntryRepository.deleteAll();
        parkingSessionRepository.deleteAll();
        spotRepository.deleteAll();
        sectorRepository.deleteAll();
    }

    @Test
    @DisplayName("Should process full cycle (Entry -> Parked -> Exit) and generate revenue")
    void shouldProcessGenericParkingCycle() throws Exception {
        // ==============================================================================================
        // ARRANGE
        // ==============================================================================================
        String licensePlate = "TEST-1234";
        Double targetLat = -23.561544;
        Double targetLng = -46.655841;
        Instant entryTime = Instant.now().minus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS); // Entrou 2 horas atrás
        Instant exitTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        Sector sectorA = new Sector();
        sectorA.setName("A");
        sectorA.setBasePrice(new BigDecimal("10.00"));
        sectorA.setMaxCapacity(20);
        sectorRepository.save(sectorA);

        Spot spot = new Spot();
        spot.setLatitude(targetLat);
        spot.setLongitude(targetLng);
        spot.setOccupied(false);
        spot.setSectorName("A");
        spot = spotRepository.save(spot);

        // Payloads para os webhooks
        // Uso da nova classe importada
        WebhookEventTestDto entryEvent = new WebhookEventTestDto("ENTRY", licensePlate, entryTime, null, null);
        WebhookEventTestDto parkedEvent = new WebhookEventTestDto("PARKED", licensePlate, null, targetLat, targetLng);
        WebhookEventTestDto exitEvent = new WebhookEventTestDto("EXIT", licensePlate, exitTime, null, null);

        // ==============================================================================================
        // ACT (Step 1: Entry Event)
        // ==============================================================================================
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryEvent)))
                .andExpect(status().isOk());

        // Verificação intermediária: Sessão criada?
        Optional<ParkingSession> sessionAfterEntry = parkingSessionRepository.findByLicensePlate(licensePlate);
        assertThat(sessionAfterEntry).isPresent();

        // ==============================================================================================
        // ACT (Step 2: Parking Event)
        // ==============================================================================================
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkedEvent)))
                .andExpect(status().isOk());

        ParkingSession sessionAfterParked = parkingSessionRepository.findByLicensePlate(licensePlate).get();
        assertThat(sessionAfterParked.getSectorName()).isEqualTo("A");
        assertThat(sessionAfterParked.getAppliedPrice()).isNotNull();

        Spot spotAfterParked = spotRepository.findById(spot.getId()).get();
        assertThat(spotAfterParked.isOccupied()).isTrue();

        // ==============================================================================================
        // ACT (Step 3: Exit Event)
        // ==============================================================================================
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exitEvent)))
                .andExpect(status().isOk());

        // ==============================================================================================
        // ASSERT
        // ==============================================================================================

        // 1. Remove active session
        Optional<ParkingSession> sessionAfterExit = parkingSessionRepository.findByLicensePlate(licensePlate);
        assertThat(sessionAfterExit).isEmpty();

        // 2. Spot must be free again
        Spot spotAfterExit = spotRepository.findById(spot.getId()).get();
        assertThat(spotAfterExit.isOccupied()).isFalse();

        // 3. Check if revenue entry was created correctly
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        List<RevenueEntry> dailyRevenues = revenueEntryRepository.findByDate(today);

        assertThat(dailyRevenues).isNotEmpty();

        // Filtra para garantir que pegamos o do nosso teste (caso o banco não tenha limpado corretamente, o que o @BeforeEach previne)
        RevenueEntry entry = dailyRevenues.stream()
                .filter(r -> r.getLicensePlate().equals(licensePlate))
                .findFirst()
                .orElseThrow(() -> new AssertionError("RevenueEntry for license plate " + licensePlate + " not found"));

        assertThat(entry.getSectorName()).isEqualTo("A");
        assertThat(entry.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(entry.getEntryTime()).isNotNull();
        assertThat(entry.getExitTime()).isNotNull();
    }

    @Test
    @DisplayName("GET /revenue should return aggregated total for specific sector and date")
    void shouldreturnAggregatedRevenueApi() throws Exception {
        // ==============================================================================================
        // ARRANGE: Set data directly in the repository to focus on testing the GET /revenue endpoint
        // ==============================================================================================
        LocalDate referenceDate = LocalDate.now(ZoneId.of("UTC"));
        String sectorName = "B";

        // 2 different revenue entries for the same sector and date
        RevenueEntry rev1 = new RevenueEntry();
        rev1.setSectorName(sectorName);
        rev1.setDate(referenceDate);
        rev1.setLicensePlate("CAR-1");
        rev1.setTotalAmount(new BigDecimal("15.50"));
        rev1.setEntryTime(Instant.now());
        rev1.setExitTime(Instant.now());

        RevenueEntry rev2 = new RevenueEntry();
        rev2.setSectorName(sectorName);
        rev2.setDate(referenceDate);
        rev2.setLicensePlate("CAR-2");
        rev2.setTotalAmount(new BigDecimal("10.00"));
        rev2.setEntryTime(Instant.now());
        rev2.setExitTime(Instant.now());

        // Dirty input: revenue from another sector that should not be counted
        RevenueEntry revNoise = new RevenueEntry();
        revNoise.setSectorName("C"); // Setor diferente
        revNoise.setDate(referenceDate);
        revNoise.setLicensePlate("CAR-3");
        revNoise.setTotalAmount(new BigDecimal("100.00"));
        revNoise.setEntryTime(Instant.now());
        revNoise.setExitTime(Instant.now());

        revenueEntryRepository.save(rev1);
        revenueEntryRepository.save(rev2);
        revenueEntryRepository.save(revNoise);


        RevenueRequest request = new RevenueRequest(referenceDate, sectorName);

        // ==============================================================================================
        // ACT & ASSERT
        // ==============================================================================================
        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency", is("BRL")))
                // O valor esperado é 15.50 + 10.00 = 25.50
                .andExpect(jsonPath("$.amount", is(25.50)));
    }
}
