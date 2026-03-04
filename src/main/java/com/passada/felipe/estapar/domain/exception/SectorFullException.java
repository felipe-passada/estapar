package com.passada.felipe.estapar.domain.exception;

public class SectorFullException extends BusinessException{

    public SectorFullException(String sectorName) {
        super("Sector " + sectorName + " is full");
    }
}
