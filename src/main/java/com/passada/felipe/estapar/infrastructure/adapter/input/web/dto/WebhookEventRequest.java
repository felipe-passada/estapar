package com.passada.felipe.estapar.infrastructure.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;

public record WebhookEventRequest(
        @JsonProperty("event_type") EventType type,
        @JsonProperty("license_plate") String plate,
        @JsonProperty("entry_time") Instant entryTime,
        @JsonProperty("exit_time") Instant exitTime,
        Double lat,
        Double lng
) {
    public enum EventType {
        ENTRY,
        PARKED,
        EXIT
    }
}
