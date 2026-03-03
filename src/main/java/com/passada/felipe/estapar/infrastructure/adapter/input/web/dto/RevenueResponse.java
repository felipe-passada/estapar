package com.passada.felipe.estapar.infrastructure.adapter.input.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RevenueResponse(
        BigDecimal amount,
        String currency,
        Instant timestamp
) {}
