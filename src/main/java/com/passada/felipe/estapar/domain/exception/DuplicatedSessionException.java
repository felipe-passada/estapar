package com.passada.felipe.estapar.domain.exception;

public class DuplicatedSessionException extends BusinessException {

    public DuplicatedSessionException(String licensePlate) {
        super("Vehicle with license plate " + licensePlate + " already has an active session");
    }
}
