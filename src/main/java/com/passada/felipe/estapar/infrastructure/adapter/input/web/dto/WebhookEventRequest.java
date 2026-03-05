package com.passada.felipe.estapar.infrastructure.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record WebhookEventRequest(
        @JsonProperty("event_type") EventType type,
        @JsonProperty("license_plate") String plate,

        @JsonProperty("entry_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        Instant entryTime,

        @JsonProperty("exit_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        Instant exitTime,

        Double lat,
        Double lng
) {
    public enum EventType {
        ENTRY,
        PARKED,
        EXIT
    }
}
