package com.satellite.domain.port.out;

import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port (Outbound) — Satellite persistence abstraction.
 *
 * <p>The domain defines this interface; the infrastructure adapter implements it.
 * This is the Repository pattern in hexagonal architecture.
 *
 * <p>Crucially, this interface uses DOMAIN types (Satellite, SatelliteId),
 * NOT JPA entities or database types.
 */
public interface SatelliteRepository {

    /**
     * Persists a satellite (create or update).
     */
    Satellite save(Satellite satellite);

    /**
     * Finds a satellite by ID.
     */
    Optional<Satellite> findById(SatelliteId id);

    /**
     * Returns all satellites.
     */
    List<Satellite> findAll();

    /**
     * Checks existence without loading the full aggregate.
     */
    boolean existsById(SatelliteId id);
}
