package com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository;

import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.RevenueEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaRevenueEntryRepository extends JpaRepository<RevenueEntryEntity, Long> {

    List<RevenueEntryEntity> findBySectorNameAndDate(String sectorName, LocalDate date);
    List<RevenueEntryEntity> findByDate(LocalDate date);
}
