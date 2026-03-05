package com.passada.felipe.estapar.infrastructure.config;

import com.passada.felipe.estapar.domain.model.Sector;
import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.SectorRepository;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.infrastructure.adapter.output.http.GarageSimulatorClient;
import com.passada.felipe.estapar.infrastructure.adapter.output.http.dto.GarageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarageInitializer {

    private final GarageSimulatorClient garageSimulatorClient;
    private final SectorRepository sectorRepository;
    private final SpotRepository spotRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        log.info("Iniciando carga de dados da garagem...");

        try {
            GarageResponse garageData = garageSimulatorClient.fetchGarageData();

            if(garageData == null) {
                log.warn("Nenhum dado recebido do simulador de garagem. Inicialização abortada.");
                return;
            }

            initializeSectors(garageData);
            initializeSpots(garageData);
            log.info("Carga de dados da garagem concluída com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao carregar dados da garagem: {}", e.getMessage(), e);
        }
    }

    private void initializeSectors(GarageResponse garageData) {
        if(garageData.garage() == null || garageData.garage().isEmpty()) {
            log.warn("Nenhum setor encontrado nos dados da garagem. Nenhum setor será inicializado.");
            return;
        }

        Set<String> incomingSectorNames = garageData.garage().stream()
                .map(GarageResponse.SectorData::sector)
                .collect(Collectors.toSet());

        Set<String> existingNames = sectorRepository.findAllByName(incomingSectorNames).stream()
                .map(Sector::getName)
                .collect(Collectors.toSet());

        List<Sector> newSectors = garageData.garage().stream()
                .filter(sd -> !existingNames.contains(sd.sector()))
                .map(sd -> new Sector(
                        sd.sector(),
                        sd.basePrice(),
                        sd.maxCapacity(),
                        sd.openHour(),
                        sd.closeHour(),
                        sd.durationLimitMinutes()
                ))
                .toList(); // Java 16+

        if (newSectors.isEmpty()) {
            log.info("Todos os setores já existem.");
            return;
        }

        sectorRepository.saveAll(newSectors);
        log.info("{} novos setores importados.", newSectors.size());
    }

    private void initializeSpots(GarageResponse garageData) {
        if (garageData.spots() == null || garageData.spots().isEmpty()) {
            log.warn("Nenhuma vaga recebida do simulador.");
            return;
        }

        Set<String> existingSpotKeys = spotRepository.findAll().stream()
                .map(spot -> spot.getLatitude() + "_" + spot.getLongitude())
                .collect(Collectors.toSet());

        List<Spot> newSpots = garageData.spots().stream()
            .filter(spot -> !existingSpotKeys.contains(spot.lat() + "_" + spot.lng()))
            .map(spot -> new Spot(
                        null,
                        spot.sector(),
                        spot.lat(),
                        spot.lng(),
                        spot.occupied()
            ))
            .toList();

        if (newSpots.isEmpty()) {
            log.info("Todas as vagas já existem no banco.");
            return;
        }

        spotRepository.saveAll(newSpots);
        log.info("{} novas vagas persistidas com sucesso.", newSpots.size());
    }
}
