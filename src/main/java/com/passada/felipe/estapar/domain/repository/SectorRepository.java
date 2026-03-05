package com.passada.felipe.estapar.domain.repository;

import com.passada.felipe.estapar.domain.model.Sector;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SectorRepository {

    Optional<Sector> findByName(String name);

    Sector save(Sector sector);

    List<Sector> findAll();

    List<Sector> findAllByName(Set<String> name);

    void saveAll(List<Sector> newSectors);

    void deleteAll();
}
