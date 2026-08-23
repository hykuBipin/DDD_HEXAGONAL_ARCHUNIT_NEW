package com.satellite.infrastructure.adapter.out.persistence;

import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.port.out.SatelliteRepository;
import com.satellite.infrastructure.adapter.out.persistence.entity.SatelliteJpaEntity;
import com.satellite.infrastructure.adapter.out.persistence.entity.SatelliteJpaRepository;
import com.satellite.infrastructure.adapter.out.persistence.mapper.SatellitePersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Driven Adapter (Outbound JPA Persistence Adapter).
 *
 * <p>Implements the SatelliteRepository outbound port defined in domain.port.out.
 * Converts domain objects to JPA entities, delegates to SatelliteJpaRepository,
 * and converts JPA entities back to domain aggregates.
 */
@Component
public class SatelliteJpaAdapter implements SatelliteRepository {

    private final SatelliteJpaRepository jpaRepository;
    private final SatellitePersistenceMapper mapper;

    public SatelliteJpaAdapter(SatelliteJpaRepository jpaRepository,
                               SatellitePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public Satellite save(Satellite satellite) {
        SatelliteJpaEntity entity = mapper.toJpaEntity(satellite);
        SatelliteJpaEntity saved  = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Satellite> findById(SatelliteId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Satellite> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public boolean existsById(SatelliteId id) {
        return jpaRepository.existsById(id.value());
    }
}
