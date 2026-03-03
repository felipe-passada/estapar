package com.passada.felipe.estapar.application.usecases;

public interface ProcessParkedUseCase {

    void execute(String licensePlate, Double latitude, Double longitude);
}
