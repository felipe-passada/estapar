package com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository;

import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaSectorRepository extends JpaRepository<SectorEntity, String> {

    Optional<SectorEntity> findByName(String name);
    List<SectorEntity> findByNameIn(Set<String> names);

}
