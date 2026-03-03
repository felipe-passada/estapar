package com.passada.felipe.estapar.application.usecases;

import com.passada.felipe.estapar.domain.model.RevenueEntry;

import java.time.Instant;

public interface ProcessExitUseCase {

    void execute(String licensePlate, Instant exitTime);
}
