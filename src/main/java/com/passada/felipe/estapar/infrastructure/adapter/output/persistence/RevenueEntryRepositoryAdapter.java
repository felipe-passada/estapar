package com.passada.felipe.estapar.infrastructure.adapter.output.persistence;

import com.passada.felipe.estapar.domain.model.RevenueEntry;
import com.passada.felipe.estapar.domain.repository.RevenueEntryRepository;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.RevenueEntryEntity;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository.JpaRevenueEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RevenueEntryRepositoryAdapter implements RevenueEntryRepository {

    private final JpaRevenueEntryRepository jpaRevenueEntryRepository;

    @Override
    public RevenueEntry save(RevenueEntry revenueEntry) {
        RevenueEntryEntity entity = toEntity(revenueEntry);
        RevenueEntryEntity saved = jpaRevenueEntryRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<RevenueEntry> findBySectorNameAndDate(String sectorName, LocalDate date) {
        return jpaRevenueEntryRepository.findBySectorNameAndDate(sectorName, date).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<RevenueEntry> findByDate(LocalDate date) {
        return jpaRevenueEntryRepository.findByDate(date)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaRevenueEntryRepository.deleteAll();
    }


    private RevenueEntry toDomain(RevenueEntryEntity entity) {
        return RevenueEntry.builder()
                .id(entity.getId())
                .licensePlate(entity.getLicensePlate())
                .sectorName(entity.getSectorName())
                .entryTime(entity.getEntryTime())
                .exitTime(entity.getExitTime())
                .totalAmount(entity.getTotalAmount())
                .date(entity.getDate())
                .build();
    }

    private RevenueEntryEntity toEntity(RevenueEntry revenueEntry) {
        return RevenueEntryEntity.builder()
                .id(revenueEntry.getId())
                .licensePlate(revenueEntry.getLicensePlate())
                .sectorName(revenueEntry.getSectorName())
                .entryTime(revenueEntry.getEntryTime())
                .exitTime(revenueEntry.getExitTime())
                .totalAmount(revenueEntry.getTotalAmount())
                .date(revenueEntry.getDate())
                .build();
    }
}
