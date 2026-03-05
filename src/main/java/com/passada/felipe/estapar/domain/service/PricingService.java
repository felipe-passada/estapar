package com.passada.felipe.estapar.domain.service;

import java.math.BigDecimal;
import java.time.Instant;

public interface PricingService {
    /**
     * Based on sector occupancy return a price modifier to be applied on top of the base price.
     *
     * @param basePrice  sector base price
     * @param sectorName name of the sector where the price is being calculated
     * @return hourly price to be applied at entry
     */
    BigDecimal calculateAppliedPrice(BigDecimal basePrice, String sectorName);

    /**
     * Return the price to be charged at exit based on the time parked and the price applied at entry.
     *
     * @param entryTime moment when the vehicle entered the parking lot
     * @param exitTime moment when the vehicle is leaving the parking lot
     * @param appliedPrice estimated hourly price calculated at entry
     * @return total price to be charged at exit
     */
    BigDecimal calculateFinalAmount(Instant entryTime, Instant exitTime, BigDecimal appliedPrice);
}
