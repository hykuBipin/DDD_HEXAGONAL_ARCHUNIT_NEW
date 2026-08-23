package com.satellite.infrastructure.adapter.in.rest.dto;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.SatelliteStatus;

import java.time.Instant;
import java.util.UUID;

public record SatelliteResponse(
        UUID id,
        String name,
        String owner,
        SatelliteStatus status,
        double altitudeKm,
        double inclinationDegrees,
        Orbit.OrbitType orbitType,
        Double batteryPercentage,
        Double signalStrengthDbm,
        Double temperatureCelsius,
        boolean isAnomalous,
        Instant registeredAt,
        Instant lastUpdatedAt
) {}
