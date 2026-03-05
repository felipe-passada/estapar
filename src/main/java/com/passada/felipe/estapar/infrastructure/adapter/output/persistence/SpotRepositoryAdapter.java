package com.passada.felipe.estapar.infrastructure.adapter.output.persistence;

import com.passada.felipe.estapar.domain.model.Spot;
import com.passada.felipe.estapar.domain.repository.SpotRepository;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.entity.SpotEntity;
import com.passada.felipe.estapar.infrastructure.adapter.output.persistence.repository.JpaSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpotRepositoryAdapter implements SpotRepository {

    private final JpaSpotRepository jpaSpotRepository;

    @Override
    public Optional<Spot> findById(Long id) {
        return jpaSpotRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Spot> findByLatitudeAndLongitude(Double lat, Double lng) {
        return jpaSpotRepository.findByLatAndLng(lat, lng)
                .map(this::toDomain);
    }

    @Override
    public List<Spot> findBySectorName(String sectorName) {
        return jpaSpotRepository.findBySectorName(sectorName).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Spot save(Spot spot) {
        SpotEntity entity = toEntity(spot);
        SpotEntity saved = jpaSpotRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Spot> findAll() {
        return jpaSpotRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Spot> spots) {
        List<SpotEntity> entities = spots.stream()
                .map(this::toEntity)
                .toList();
        jpaSpotRepository.saveAll(entities);
    }

    @Override
    public void deleteAll() {
        jpaSpotRepository.deleteAll();
    }

    private Spot toDomain(SpotEntity entity) {
        return Spot.builder()
                .id(entity.getId())
                .sectorName(entity.getSectorName())
                .latitude(entity.getLat())
                .longitude(entity.getLng())
                .occupied(entity.getOccupied())
                .build();
    }

    private SpotEntity toEntity(Spot spot) {
        return SpotEntity.builder()
                .id(spot.getId())
                .sectorName(spot.getSectorName())
                .lat(spot.getLatitude())
                .lng(spot.getLongitude())
                .occupied(spot.isOccupied())
                .build();
    }
}
