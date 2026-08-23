package com.satellite.domain.port.in;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.Satellite;

/**
 * Driving Port (Inbound Use Case) — Launch a Satellite.
 *
 * <p>This interface is part of the domain. Application services implement it.
 * REST controllers and other adapters depend on this port, NOT on the implementation.
 */
public interface LaunchSatelliteUseCase {

    record LaunchCommand(String name, String owner, Orbit orbit) {}

    /**
     * Registers and launches a new satellite.
     *
     * @param command the satellite launch parameters
     * @return the newly created and launched satellite
     */
    Satellite launch(LaunchCommand command);
}
