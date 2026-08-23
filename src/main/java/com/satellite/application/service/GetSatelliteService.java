package com.satellite.application.service;

import com.satellite.domain.exception.SatelliteDomainException;
import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.port.in.GetSatelliteUseCase;
import com.satellite.domain.port.out.SatelliteRepository;

import java.util.List;

/**
 * Application Service — implements GetSatelliteUseCase (query side).
 */
public class GetSatelliteService implements GetSatelliteUseCase {

    private final SatelliteRepository repository;

    public GetSatelliteService(SatelliteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Satellite getById(SatelliteId id) {
        return repository.findById(id)
                .orElseThrow(() -> new SatelliteDomainException(
                        "Satellite not found with id: " + id));
    }

    @Override
    public List<Satellite> getAll() {
        return repository.findAll();
    }
}
