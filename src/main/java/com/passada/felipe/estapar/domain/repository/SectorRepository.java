package com.passada.felipe.estapar.domain.repository;

import com.passada.felipe.estapar.domain.model.Sector;

import java.util.List;
import java.util.Optional;

public interface SectorRepository {

    Optional<Sector> findByName(String name);

    Sector save(Sector sector);

    List<Sector> findAll();
}
