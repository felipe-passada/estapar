package com.passada.felipe.estapar.integration.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WebhookEventTestDto {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    @JsonProperty("event_type")
    public String type;

    @JsonProperty("license_plate")
    public String licensePlate;

    @JsonProperty("entry_time")
    public String entryTime;

    @JsonProperty("exit_time")
    public String exitTime;

    public Double lat;
    public Double lng;

    public WebhookEventTestDto(String type, String licensePlate, Instant entryTime, Double lat, Double lng) {
        this.type = type;
        this.licensePlate = licensePlate;
        this.lat = lat;
        this.lng = lng;

        if (entryTime != null) {
            this.entryTime = FORMATTER.format(entryTime);
        }
        if ("EXIT".equals(type) && entryTime != null) {
            this.exitTime = FORMATTER.format(entryTime);
            this.entryTime = null;
        }
    }
}