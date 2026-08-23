package com.satellite.adapter.out.persistence;

import com.satellite.adapter.out.persistence.entity.SatelliteJpaRepository;
import com.satellite.adapter.out.persistence.mapper.SatellitePersistenceMapper;
import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.port.out.SatelliteRepository;

import java.util.List;
import java.util.Optional;

/**
 * Driven Adapter — implements the SatelliteRepository domain port using JPA.
 *
 * <p>This is where Spring Data, H2, and JPA live. The domain never sees this class.
 * All the messy infrastructure concern is contained here.
 */
public class SatelliteJpaAdapter implements SatelliteRepository {

    private final SatelliteJpaRepository jpaRepository;

    public SatelliteJpaAdapter(SatelliteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Satellite save(Satellite satellite) {
        var entity = SatellitePersistenceMapper.toJpaEntity(satellite);
        var saved  = jpaRepository.save(entity);
        return SatellitePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Satellite> findById(SatelliteId id) {
        return jpaRepository.findById(id.value())
                .map(SatellitePersistenceMapper::toDomain);
    }

    @Override
    public List<Satellite> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(SatellitePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(SatelliteId id) {
        return jpaRepository.existsById(id.value());
    }
}
