package com.passada.felipe.estapar.infrastructure.adapter.output.http;

import com.passada.felipe.estapar.infrastructure.adapter.output.http.dto.GarageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarageSimulatorClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${garage.simulator.url}")
    private String simulatorBaseUrl;

    public GarageResponse fetchGarageData() {
        log.info("Buscando dados da garagem no simulador: {}/garage", simulatorBaseUrl);

        RestClient restClient = restClientBuilder.baseUrl(simulatorBaseUrl).build();

        GarageResponse response = restClient
                .get()
                .uri("/garage")
                .retrieve()
                .body(GarageResponse.class);


        var totalSectors = response != null && response.garage() != null
                ? response.garage().size()
                : 0;

        var totalSpots = response != null && response.garage() != null
                ? response.spots().size()
                : 0;

        log.info("Successfully retrieved garage data. Sectors: {}, Spots: {}", totalSectors, totalSpots);


        return response;
    }
}
