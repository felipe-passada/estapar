package com.passada.felipe.estapar.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GetRevenueUseCase {

    BigDecimal execute(String sectorName, LocalDate date);
}
