package com.passada.felipe.estapar.domain.service;

public interface OccupancyService {
    /**
     *
     * @param sectorName
     * @return occupancy percentual (0.0 to 1.0)
     */
    public double getOccupancyRate(String sectorName);
    /**
     *
     * @param sectorName
     * @return true if sector is full
     */
    public boolean isSectorFull(String sectorName);
    /**
     *
     * @return true if all sectors are full (garage is full)
     */
    public boolean isGarageFull();
}
