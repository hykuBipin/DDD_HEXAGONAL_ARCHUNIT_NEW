package com.satellite.domain.port.in;

import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;

import java.util.List;

/**
 * Driving Port — Query satellite information.
 */
public interface GetSatelliteUseCase {

    /**
     * Retrieves a satellite by its unique ID.
     *
     * @param id the satellite identity
     * @return the satellite aggregate
     * @throws com.satellite.domain.exception.SatelliteDomainException if not found
     */
    Satellite getById(SatelliteId id);

    /**
     * Returns all registered satellites.
     */
    List<Satellite> getAll();
}
