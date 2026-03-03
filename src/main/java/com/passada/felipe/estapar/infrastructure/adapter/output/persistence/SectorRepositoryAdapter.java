package com.passada.felipe.estapar.infrastructure.adapter.output.persistence;

import com.passada.felipe.estapar.domain.model.Sector;
import com.passada.felipe.estapar.domain.repository.SectorRepository;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.SectorEntity;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository.JpaSectorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class SectorRepositoryAdapter implements SectorRepository {
    private final JpaSectorRepository jpaSectorRepository;

    public SectorRepositoryAdapter(JpaSectorRepository jpaSectorRepository) {
        this.jpaSectorRepository = jpaSectorRepository;
    }

    @Override
    public Optional<Sector> findByName(String name) {
        return jpaSectorRepository.findById(name)
                .map(this::toDomain);
    }

    @Override
    public Sector save(Sector sector) {
        SectorEntity entity = toEntity(sector);
        SectorEntity saved = jpaSectorRepository.save(entity);
        return toDomain(saved);
    }

    //TODO: Implements findAll method to return all sectors from database
    @Override
    public List<Sector> findAll() {
        return jpaSectorRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Sector> findAllByName(Set<String> names) {
        return jpaSectorRepository.findByNameIn(names)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Sector> sectors) {
        List<SectorEntity> entities = sectors.stream()
                .map(this::toEntity)
                .toList();
        jpaSectorRepository.saveAll(entities);
    }

    private Sector toDomain(SectorEntity entity) {
        return Sector.builder()
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .maxCapacity(entity.getMaxCapacity())
                .openHour(entity.getOpenHour())
                .closeHour(entity.getCloseHour())
                .durationLimitMinutes(entity.getDurationLimitMinutes())
                .build();
    }

    private SectorEntity toEntity(Sector sector) {
        return SectorEntity.builder()
                .name(sector.getName())
                .basePrice(sector.getBasePrice())
                .maxCapacity(sector.getMaxCapacity())
                .openHour(sector.getOpenHour())
                .closeHour(sector.getCloseHour())
                .durationLimitMinutes(sector.getDurationLimitMinutes())
                .build();
    }
}
