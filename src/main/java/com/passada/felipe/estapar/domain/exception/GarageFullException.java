package com.passada.felipe.estapar.domain.exception;

public class GarageFullException extends BusinessException {

    public GarageFullException(String licensePlate) {
        super("Every sector in the garage is full, entry denied for license plate: " + licensePlate);
    }
}
