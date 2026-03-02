package com.passada.felipe.estapar.domain.repository;

import com.passada.felipe.estapar.domain.model.RevenueEntry;

import java.time.LocalDate;
import java.util.List;

public interface RevenueEntryRepository {

    RevenueEntry save(RevenueEntry revenueEntry);

    List<RevenueEntry> findBySectorNameAndDate(String sectorName, LocalDate date);

    List<RevenueEntry> findByDate(LocalDate date);
}
