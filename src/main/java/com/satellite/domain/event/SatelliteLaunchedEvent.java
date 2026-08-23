package com.satellite.domain.event;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.SatelliteId;

import java.time.Instant;

/**
 * Domain Event — raised when a satellite is successfully launched.
 *
 * <p>Domain events are immutable records. They represent something that
 * happened in the domain and cannot be changed after the fact.
 */
public record SatelliteLaunchedEvent(
        SatelliteId satelliteId,
        String satelliteName,
        Orbit orbit,
        Instant occurredAt
) {}
