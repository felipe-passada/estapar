package com.passada.felipe.estapar.domain.exception;

public class SpotAlredyOccupiedException extends BusinessException{

    public SpotAlredyOccupiedException(Long spotId) {
        super("Spot " + spotId + " is already occupied");
    }
}
