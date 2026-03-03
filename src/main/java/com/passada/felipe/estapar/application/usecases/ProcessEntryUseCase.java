package com.passada.felipe.estapar.application.usecases;

import java.time.Instant;

public interface ProcessEntryUseCase {

    void execute(String licensePlate, Instant entryTime);
}
