package com.passada.felipe.estapar.application.services;

import com.passada.felipe.estapar.application.usecases.GetRevenueUseCase;
import com.passada.felipe.estapar.domain.model.RevenueEntry;
import com.passada.felipe.estapar.domain.repository.RevenueEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRevenueService implements GetRevenueUseCase {

    private final RevenueEntryRepository revenueEntryRepository;

    @Override
    public BigDecimal execute(String sectorName, LocalDate date) {
        List<RevenueEntry> entries = revenueEntryRepository.findBySectorNameAndDate(sectorName, date);

        return entries.stream()
                .map(RevenueEntry::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
