package com.passada.felipe.estapar.infrastructure.adapter.input.web.dto;

import java.time.Instant;

public record ErrorResponse(String message, int status, Instant timestamp) {
}
